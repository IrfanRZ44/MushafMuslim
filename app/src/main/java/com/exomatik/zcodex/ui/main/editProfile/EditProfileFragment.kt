package com.exomatik.zcodex.ui.main.editProfile

import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentEditProfileBinding

class EditProfileFragment : BaseFragmentBind<FragmentEditProfileBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_edit_profile
    lateinit var viewModel: EditProfileViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = EditProfileViewModel(findNavController(), savedData, activity)
        bind.viewModel = viewModel

        onClick()
    }

    private fun onClick() {

    }

}