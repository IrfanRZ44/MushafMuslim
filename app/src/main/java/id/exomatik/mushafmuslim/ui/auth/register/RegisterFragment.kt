package id.exomatik.mushafmuslim.ui.auth.register

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import androidx.navigation.fragment.findNavController
import id.exomatik.mushafmuslim.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragmentBind<FragmentRegisterBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_register
    lateinit var viewModel: RegisterViewModel

    override fun myCodeHere() {
        supportActionBar?.hide()
        bind.lifecycleOwner = this
        viewModel = RegisterViewModel(activity, savedData, findNavController())
        bind.viewModel = viewModel
        onClick()
    }

    private fun onClick() {
        bind.etPhone.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.cekUserName()
                return@OnEditorActionListener false
            }
            false
        })
    }
}