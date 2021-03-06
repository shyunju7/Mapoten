package com.mapo.mapoten.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mapo.mapoten.R
import com.mapo.mapoten.config.RetrofitBuilder
import com.mapo.mapoten.data.ImageResponse
import com.mapo.mapoten.data.UpdateBusinessProfileItems
import com.mapo.mapoten.databinding.FragmentBusinessProfile01Binding
import com.mapo.mapoten.databinding.FragmentBusinessProfileEditBinding
import com.mapo.mapoten.service.AccountManageService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class BusinessProfileEdit : Fragment() {


    lateinit var binding: FragmentBusinessProfileEditBinding
    lateinit var service: AccountManageService
    private var selectedImageUri: Uri? = null
    private var filePath: String = ""
    private var code = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinessProfileEditBinding.inflate(inflater, container, false)

        val view = binding.root

        service = RetrofitBuilder.getInstance().create(AccountManageService::class.java)

        binding.backButton.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        initiateLogoUpload()
        // initiateFileUpload()

        initData()
        checkBizCode()


        binding.businessSaveButton.setOnClickListener {
            val check = checkAllNotEmpty()

            if (check) {
                if (selectedImageUri == null) {
                    Log.d("profile", "??????????????? ???????????? ??????")
                    addCompProfile()

                } else {
                    addCompProfile()
                    addCompImg()
                }
            }else{
                Log.d("profile", "?????????????????? ????????????----")
            }
        }
        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            2000 -> {
                selectedImageUri = data?.data!!
                if (selectedImageUri != null) {
                    binding.businessLogo.setImageURI(selectedImageUri)
                    binding.iconImageUpload.visibility = GONE
                    filePath = getImageFilePath(selectedImageUri!!)
                    Log.d("profile", "????????????: $filePath")
                    binding.businessSaveButton.setOnClickListener {
                        Toast.makeText(requireContext(), "??????????????? ??????", Toast.LENGTH_SHORT).show()
                        addCompProfile()
                        addCompImg()
                        Log.d("profile", "????????? uri : $selectedImageUri")
                    }
                } else {
                    Toast.makeText(requireContext(), "????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show()
                }

            }

            3000 -> {
                val selectedFile: Uri? = data?.data
                if (selectedFile != null) {
                    val file = selectedFile!!
                  //  binding.fileView.text = file.encodedPath
                //    binding.iconFileUpload.visibility = GONE
                  //  binding.fileView.visibility = VISIBLE
                }
            }

        }
    }

    private fun initData(){
        binding.businessNameText.setText(arguments?.getString("cmpny_nm"))
        binding.businessNumberText.setText(arguments?.getString("bizrno"))
        binding.ownerNameText.setText(arguments?.getString("ceo"))
        binding.businessEmailText.setText(arguments?.getString("cmpny_email"))
        binding.businessAddressText.setText(arguments?.getString("address"))
        binding.businessAddressDetailText.setText(arguments?.getString("detailad"))
        binding.businessCategoryText.setText(arguments?.getString("category"))
        binding.businessEmployeeNumberText.setText(arguments?.getString("empNum"))
        binding.businessWebsiteText.setText(arguments?.getString("webSite"))

        // Glide.with(binding.imgBusinessLogoValue).load(img).into(binding.imgBusinessLogoValue)

    }

    private fun initiateLogoUpload() {
        binding.iconImageUpload.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("logo", "1 ??? ??????")
                    //??????????????? ?????? ??????
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //??????????????? ??????
                    showPermissionContextPopup()
                    Log.d("logo", "2 ??? ??????")

                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                    Log.d("logo", "3 ??? ??????")
                }
            }
        }
    }

    private fun checkBizCode() {
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            code = when(i){
                R.id.radio_button_1 -> {
                    "10"
                }
                R.id.radio_button_2 -> {
                    "20"
                }
                else -> {
                    "30"
                }
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("????????? ???????????????")
            .setMessage("????????? ???????????? ?????? ?????? ??????")
            .setPositiveButton("????????????", { dialog, which ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            })
            .setNegativeButton("????????????", { _, _ -> })
            .create()
            .show()
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)

    }


   /* private fun initiateFileUpload() {
        binding.iconFileUpload.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("file", "?????? 1 ??? ??????")
                    //?????? ??????
                    navigateFiles()

                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //??????????????? ??????
                    showPermissionContextPopup()
                    Log.d("logo", "2 ??? ??????")

                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                    Log.d("logo", "3 ??? ??????")
                }
            }
        }
    }*/

    private fun navigateFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, 3000)

    }

    private fun addCompProfile() {
        Log.d("profile", "??????????????? ???????????? ??????-----------")
        val compName = binding.businessNameText.text.toString()
        val compNum = binding.businessNumberText.text.toString()
        val ceoName = binding.ownerNameText.text.toString()
        val email = binding.businessEmailText.text.toString()
        val address = binding.businessAddressText.text.toString()
        val detailAd = binding.businessAddressDetailText.text.toString()
        val category = binding.businessCategoryText.text.toString()
        val empNum = binding.businessEmployeeNumberText.text.toString()
        val homepage = binding.businessWebsiteText.text.toString()
        val bizcode = code
        Log.d("check", "??????????????? ?????? code : $bizcode")
        //?????? ????????? ?????? ????????? ???????????????, ?????? ?????? ?????? ??????????????????.

        val profile = UpdateBusinessProfileItems(
            compName,
            email,
            bizcode,
            compName,
            compNum,
            ceoName,
            address,
            detailAd,
            category,
            empNum,
            homepage,
            email
        )

        service.updateBusinessProfile(profile).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val msg = response.message()
                    Log.d("profile ??????", "msg : $msg")
                    Toast.makeText(requireContext(), "?????? ?????? ???????????????.", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(binding.root).navigate(R.id.action_businessProfileEdit_to_businessAccount_01)
                }else {
                    Log.d("check", "code : ${response.code()}")
                    Log.d("check", "message : ${response.message()}")
                    Log.d("check", "message : ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("profile ??????", "error" + t.message)
            }
        })


    }

    private fun checkAllNotEmpty():Boolean {
        val textInput = arrayListOf<Editable?>(
            binding.businessNameText.text,binding.businessNumberText.text,
            binding.ownerNameText.text,binding.businessEmailText.text,
            binding.businessAddressText.text,binding.businessAddressDetailText.text,
            binding.businessCategoryText.text, binding.businessEmployeeNumberText.text,
            binding.businessWebsiteText.text
        )

        val textLayout = arrayListOf<TextInputLayout>(
            binding.businessName,binding.businessValidNumber,binding.ownerName,
            binding.businessEmail,binding.businessAddress1, binding.businessAddress2,
            binding.businessCategory, binding.businessEmployeeNumber,binding.businessWebsite
        )
        for(index in textInput.indices){
            Log.d("id", "????????? : $index")
            if(textInput[index].isNullOrEmpty()){
                textLayout[index].error = "?????? ???????????? ?????????."
                return false
            }else{
                textLayout[index].error = null
            }
        }

        return true
    }

    fun getImageFilePath(contentUri: Uri): String {
        Log.d("profile", "???????????? uri : $contentUri")
        var columnIndex = 0
        val column = "_data";
        val projection = arrayOf(column)
        // val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = requireContext().contentResolver.query(contentUri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            var document_id: String = cursor.getString(0);
            if (document_id == null) {
                for (i in 1..cursor.getColumnCount()) {
                    if (column.equals(cursor.getColumnName(i))) {
                        filePath = cursor.getString(i);
                        break;
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close()



                try {
                    cursor = requireContext().getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(document_id),
                        null
                    );
                    if (cursor != null) {
                        cursor.moveToFirst();
                        filePath = cursor.getString(cursor.getColumnIndexOrThrow(column));
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }

            return filePath
        }


        private fun addCompImg() {

            Log.d("profile", "??????????????? ?????? addCompimg ?????????")
            //creating a file
            if(filePath ==""){
                Log.d("profile", "file path??? ??????-----")
            }
            Thread {
                val uploadFile = File(filePath)

                val requestBody: RequestBody = RequestBody.create(selectedImageUri?.let {
                    requireContext().contentResolver.getType(it)?.toMediaTypeOrNull()
                }, uploadFile)
                val fileToUpload: MultipartBody.Part =
                    createFormData("file", uploadFile.name, requestBody)


                Log.d("profile", "??????????????? ${uploadFile.name}")
                Log.d("profile", "???????????????  body $fileToUpload")

                service.updateBusinessLogoImg(fileToUpload).enqueue(object : Callback<ImageResponse> {
                    override fun onResponse(call: Call<ImageResponse>,response: Response<ImageResponse>) {
                        if (response.isSuccessful){
                            Log.d("profile", "????????? ?????? ??????!!")
                        }
                        Log.d("profile", "????????? ?????? ${response.code()}")
                        Log.d("profile", "????????? ?????? ${response.message()}")
                        Log.d("profile", "????????? ?????? ${response.errorBody()?.string()}")

                    }

                    override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                        Log.d("profile", "????????? ?????? ??????!!")
                        Log.d("profile ??????", "error" + t.message)
                    }


                })
            }.start()

        }



}





