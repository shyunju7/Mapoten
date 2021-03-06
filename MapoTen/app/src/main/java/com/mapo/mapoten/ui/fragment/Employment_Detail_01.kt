package com.mapo.mapoten.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.mapo.mapoten.R
import com.mapo.mapoten.config.RetrofitBuilder
import com.mapo.mapoten.data.employment.BookmarkResponse
import com.mapo.mapoten.data.employment.CodeName
import com.mapo.mapoten.data.employment.EmploymentResponse
import com.mapo.mapoten.data.employment.GeneralEmpPostingDetailDTO
import com.mapo.mapoten.databinding.FragmentEmploymentDetail01Binding
import com.mapo.mapoten.service.EmploymentService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class Employment_Detail_01 : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentEmploymentDetail01Binding
    lateinit var employmentService: EmploymentService
    var type by Delegates.notNull<Int>()
    private lateinit var dialog: Dialog

    private lateinit var mapView: MapView
    private lateinit var geocoder: Geocoder
    private lateinit var geoLatLng: LatLng
    private var jobId : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEmploymentDetail01Binding.inflate(inflater, container, false)
        val view = binding.root

        employmentService = RetrofitBuilder.getInstance().create(EmploymentService::class.java)

        geocoder = Geocoder(requireContext())
        type = arguments?.getInt("type")!!
        jobId = arguments?.getInt("jobId")

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        binding.backButton.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        if (jobId != null) {
            loading(true)
            getGeneralJobPostingDetail(jobId!!)
        }

        binding.refreshLayout.setOnRefreshListener {
            if (jobId != null) {
                getGeneralJobPostingDetail(jobId!!)
            } else {
                Toast.makeText(requireContext(), "?????? ??????????????????..!", Toast.LENGTH_SHORT).show()
            }
            binding.refreshLayout.isRefreshing = false
        }

        if (arguments?.getString("dDay") === "closed") {
            binding.submitBtn.isEnabled = false
            binding.submitBtn.setBackgroundColor(Color.parseColor("#C4C4C4"))
        }

        binding.submitBtn.setOnClickListener {
            dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.popup_emp_posting_detail_submit)
            showDialog()
        }


        binding.bookmarkBtn.setOnClickListener {
            jobId?.let { registerBookmark(it) }
        }
        return view
    }


    private fun getLatLng(address: String?) {

        Log.d("EmpDetail", "address Info : $address")

        var list: ArrayList<Address>? = null

        try {

            if (address === null) {

                binding.placeOfWorkValue.text = "?????? ??????????????? ?????? ????????? ??????????????????."
                binding.mapView.visibility = View.GONE

                return
            }

            list = geocoder.getFromLocationName(address, 10) as ArrayList<Address>?

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("map", "error")
        }

        if (list != null) {
            if (list.size == 0) {
                Log.d("map", "list size : 0")

            } else {
                val lat = list[0].latitude
                val long = list[0].longitude

                Log.d("map", "list : ${list[0].latitude}")
                Log.d("map", "long : ${list[0].longitude}")


                geoLatLng = LatLng(lat, long)
                Log.d("map", "list : ${geoLatLng}")

                mapView.getMapAsync(this)

            }
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) binding.loading.visibility = View.VISIBLE
        else {
            binding.loading.visibility = View.GONE
            binding.detail.visibility = View.VISIBLE
        }
    }

    private fun getGeneralJobPostingDetail(id: Int) {

        Log.d("generalDetail", "id : " +id)


        val generalJobList = employmentService.inquireGeneralDetailPosting(id)

        generalJobList.enqueue(object : Callback<EmploymentResponse> {
            override fun onResponse(
                call: Call<EmploymentResponse>,
                response: Response<EmploymentResponse>
            ) {

                Log.d("generalDetail", "code : " + response.code())
                Log.d("generalDetail", "message : " + response.message())

                if (response.isSuccessful) {

                    Log.d("generalDetail", "resultDataList : ${response.body()}")

                    thread(start = true) {
                        Thread.sleep(200)

                        requireActivity().runOnUiThread {
                            loading(false)
                            response.body()?.data?.let { setData(it) }
                            getLatLng(response.body()?.data!!.workAddress)
                        }
                    }

                } else {
                    Log.d("generalDetail", "code : " + response.code())
                    Log.d("generalDetail", "message : " + response.message())

                }
            }

            override fun onFailure(call: Call<EmploymentResponse>, t: Throwable) {
                Log.e("generalDetail", "?????? ??????" + t.localizedMessage)

            }

        })
    }

    // bookmark
    private fun registerBookmark(id: Int) {

        Log.d("generalDetail", "id : $id")

        employmentService.registerBookmark(id).enqueue(object : Callback<Objects> {
            override fun onResponse(call: Call<Objects>, response: Response<Objects>) {
                if (response.isSuccessful) {

                    Log.d("generalDetail", "message : ${response.message()}")
                    Toast.makeText(requireContext(), "????????? ?????????????????????:)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Objects>, t: Throwable) {
                Log.e("generalDetail", "?????? ??????" + t.localizedMessage)
            }

        })
    }

    private fun setData(result: GeneralEmpPostingDetailDTO) {
        binding.category.text = if (type === 1) "????????????" else "????????????"
        binding.title.text = result.title
        binding.date.text = setDDay(result.endReception)
        if (result.jobImage != null) {
            Glide.with(requireActivity()).load(result.jobImage).into(binding.image)
        } else {
            binding.image.setImageResource(R.drawable.banner_image1)
        }
        // ????????????
        binding.jobTypeDescValue.text = result.jobTypeDesc
        binding.requireCountValue.text = result.requireCount
        binding.jobDescValue.text = result.jobDesc
        binding.educationValue.text = result.education.codeName
        binding.careerValue.text = result.career.codeName
        binding.employTypeValue.text = manufactureData(result.employTypeDet)



        Log.d("detail dept", "dept : ${result.employTypeDet}")
        Log.d("detail dept", "size : ${result.employTypeDet.size}")


        // ????????????
        Glide.with(requireActivity()).load(result.companyImage).into(binding.companyImage)
        binding.companyNameValue.text = result.name
        binding.ceoValue.text = result.ceo
        binding.addressValue.text = result.address
        binding.sectorValue.text = result.sector
        binding.quaternionValue.text = result.quaternion

        // ????????????
        binding.paycdValue.text = result.paycd.codeName
        binding.payAmountValue.text = result.payAmount
        binding.workTimeTypeValue.text = result.workTimeType.codeName
        binding.mealCodValue.text = result.mealCod.codeName
        binding.workingHoursValue.text = result.workingHours
        binding.severancePayTypeValue.text = result.severancePayType.codeName
        binding.socialInsuranceValue.text = manufactureData(result.socialInsurance)

        // ????????????
        binding.applyMethodValue.text = manufactureData(result.applyMethod)
        binding.testMethodValue.text = manufactureData(result.testMethod)
        binding.applyDocumentValue.text = manufactureData(result.applyDocument)
        binding.endReceptionValue.text = result.endReception.substring(0, 10)

        // ?????? ????????? ??????
        binding.contactNameValue.text = result.contactName
        binding.contactDepartmentValue.text = result.contactDepartment
        binding.contactPhoneValue.text = result.contactPhone
        binding.contactEmailValue.text = result.contactEmail

        // ????????????
        binding.placeOfWorkValue.text = result.workAddress

    }

    private fun manufactureData(data: ArrayList<CodeName>): String {
        var tmpText = ""
        data.forEach { it ->
            tmpText += "${it.codeName}, "
        }

        return tmpText.substring(0, tmpText.length - 2)
    }

    private fun setDDay(endDay: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val today = Calendar.getInstance()
        val endDate = dateFormat.parse(endDay.substring(0, 10))

        val day = (endDate.time - today.time.time) / (24 * 60 * 60 * 1000)

        return if (day.toString() == "0") {
            "D-day"
        } else if (day < 0) {
            "?????? ????????? ????????? ???????????????."
        } else {
            "${endDay.substring(0, 4)}??? ${
                endDay.substring(
                    5,
                    7
                )
            }??? ${endDay.substring(8, 10)}??? ????????????  D-${day}"

        }
    }

    private fun showDialog() {
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteBtn: AppCompatButton = dialog.findViewById(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            dialog.dismiss()
        }


        val closeBtn: ImageView = dialog.findViewById(R.id.closeBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(naverMap: NaverMap) {

        Log.d("map", "onMapReady()...$geoLatLng")

        val marker = Marker()
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = Color.parseColor("#7C9FF2")
        marker.position = geoLatLng
        marker.captionText = "???????????????"
        marker.captionRequestedWidth = 300
        marker.captionColor = Color.parseColor("#4C67A6")
        marker.captionHaloColor = Color.parseColor("#ffffff")
        marker.isHideCollidedSymbols = true
        marker.map = naverMap

        val cameraUpdate = CameraUpdate.scrollTo(geoLatLng)
        naverMap.moveCamera(cameraUpdate)

    }

}