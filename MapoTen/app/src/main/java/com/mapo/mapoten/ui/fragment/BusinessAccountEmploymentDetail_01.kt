package com.mapo.mapoten.ui.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.mapo.mapoten.R
import com.mapo.mapoten.config.RetrofitBuilder
import com.mapo.mapoten.data.employment.CodeName
import com.mapo.mapoten.data.employment.GeneralEmpPostingDetailDTO
import com.mapo.mapoten.data.employment.SelectJobEnterpriseDetailOutputDto
import com.mapo.mapoten.databinding.FragmentBusinessAccountEmploymentDetail01Binding
import com.mapo.mapoten.service.EmploymentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class BusinessAccountEmploymentDetail_01 : Fragment() {
    lateinit var binding: FragmentBusinessAccountEmploymentDetail01Binding
    lateinit var employmentService: EmploymentService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            FragmentBusinessAccountEmploymentDetail01Binding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.getInt("jobId")?.let { getDetail(it) }

        //getDetail(6)
        binding.state.text = arguments?.getString("state")
        changeStateBackground()


        binding.backButton.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        return view
    }


    private fun changeStateBackground() {
        val stateBackground: GradientDrawable = binding.state.background as GradientDrawable

        when (binding.state.text) {
            "승인완료" -> {
                stateBackground.setColor(Color.parseColor("#E8F1FF"))
                binding.state.setTextColor(Color.parseColor("#1A75FF"))
            }
            "승인거절" -> {
                stateBackground.setColor(Color.parseColor("#FFE8EC"))
                binding.state.setTextColor(Color.parseColor("#FF1A43"))
            }
            "승인요청" -> {
                stateBackground.setColor(Color.parseColor("#FFF6E8"))
                binding.state.setTextColor(Color.parseColor("#FFA31A"))
            }
            else -> {
                stateBackground.setColor(Color.parseColor("#EDEDED"))
                binding.state.setTextColor(Color.parseColor("#979797"))
            }
        }
    }


    private fun getDetail(id: Int) {
        employmentService = RetrofitBuilder.getInstance().create(EmploymentService::class.java)
        val employmentManagementDetail = employmentService.getEnterPriseJobDetail(id)

        employmentManagementDetail.enqueue(object : Callback<SelectJobEnterpriseDetailOutputDto> {
            override fun onResponse(
                call: Call<SelectJobEnterpriseDetailOutputDto>,
                response: Response<SelectJobEnterpriseDetailOutputDto>
            ) {
                Log.d("employmentDetail", "id : " + id)

                Log.d("employmentDetail", "code : " + response.code())
                Log.d("employmentDetail", "message : " + response.message())

                if (response.isSuccessful) {
                    Log.d("employmentDetail", "isSuccessful.. body -> : " + response.body())
                    response.body()?.let { setData(it.data) }
                } else {
                    Log.d("employmentDetail", "code : " + response.code())
                }
            }

            override fun onFailure(call: Call<SelectJobEnterpriseDetailOutputDto>, t: Throwable) {
                Log.e("employmentDetail", "통신 실패" + t.localizedMessage)
            }

        })
    }

    private fun setData(result: GeneralEmpPostingDetailDTO) {
        binding.title.text = result.title

        if (result.image != null) {
            Glide.with(requireActivity()).load(result.image).into(binding.image)
        } else {
            binding.image.setImageResource(R.drawable.banner_image1)
        }
        // 채용사항
        binding.jobTypeDescValue.text = result.jobTypeDesc
        binding.requireCountValue.text = result.requireCount
        binding.jobDescValue.text = result.jobDesc
        binding.educationValue.text = result.education
        binding.careerValue.text = result.career
        binding.employTypeValue.text = manufactureData(result.employTypeDet)


        // 업체현황
//        binding.companyNameValue.text = result.name
//        binding.ceoValue.text = result.ceo
//        binding.addressValue.text = result.address
//        binding.sectorValue.text = result.sector
        //binding.quaternionValue.text = result.quaternion

        // 근로조건
        binding.paycdValue.text = result.paycd
        binding.payAmountValue.text = result.payAmount
        binding.workTimeTypeValue.text = result.workTimeType
        binding.mealCodValue.text = result.mealCod
        binding.workingHoursValue.text = result.workingHours
        binding.severancePayTypeValue.text = result.severancePayType

        binding.socialInsuranceValue.text = manufactureData(result.socialInsurance)

        // 전형사항
        binding.applyMethodValue.text = result.applyMethod
        binding.testMethodValue.text = result.testMethod
        binding.applyDocumentValue.text = manufactureData(result.applyDocument)

        // 채용 담당자 정보
        binding.contactNameValue.text = result.contactName
        binding.contactDepartmentValue.text = result.contactDepartment
        binding.contactPhoneValue.text = result.contactPhone
        binding.contactEmailValue.text = result.contactEmail

        // 근무위치
        binding.placeOfWorkValue.text = result.workAddress

    }

    private fun manufactureData(data: ArrayList<CodeName>): String {
        var tmpText = ""
        data.forEach { it ->
            tmpText += "${it.codeName}, "
        }

        return tmpText.substring(0, tmpText.length - 2)
    }
}