package com.exomatik.zcodex.ui.main.editProfile

import android.app.Activity
import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentEditProfileBinding
import com.exomatik.zcodex.utils.setImageBox
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class EditProfileFragment : BaseFragmentBind<FragmentEditProfileBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_edit_profile
    lateinit var viewModel: EditProfileViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = EditProfileViewModel(findNavController(), savedData, activity, bind.spinnerBank)
        bind.viewModel = viewModel

        viewModel.setData()

        MobileAds.initialize(activity) {}
        bind.adView.loadAd(AdRequest.Builder().build())
        onClick()
    }

    private fun onClick() {
        bind.cardFoto.setOnClickListener {
            context?.let {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(it, this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK){
                val imageUri = result.uri
                bind.imgFoto.setBackgroundResource(android.R.color.transparent)
                setImageBox(bind.imgFoto, imageUri.toString())

                val username = savedData.getDataUser()?.username

                if (!username.isNullOrEmpty()){
                    viewModel.saveFoto(imageUri, username)
                }
                else{
                    viewModel.message.value = "Error, terjadi kesalahan database"
                }
            }
        }
    }
}