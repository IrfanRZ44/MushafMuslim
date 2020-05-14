package com.exomatik.baseapplication.ui.auth.register

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.base.BaseFragmentBind
import androidx.navigation.fragment.findNavController
import com.exomatik.baseapplication.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragmentBind<FragmentRegisterBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_register
    lateinit var viewModel: RegisterViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = RegisterViewModel(activity, savedData, findNavController())
        bind.viewModel = viewModel
        onClick()
    }

    private fun onClick() {
        bind.etPhone.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onClickRegister()
                return@OnEditorActionListener false
            }
            false
        })
    }
}