package id.exomatik.mushafmuslim.ui.main.account

import androidx.navigation.fragment.findNavController
import id.exomatik.mushafmuslim.BuildConfig
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.databinding.FragmentAccountBinding

class AccountFragment : BaseFragmentBind<FragmentAccountBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_account
    lateinit var viewModel: AccountViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = AccountViewModel(findNavController(), savedData, activity, context)
        bind.viewModel = viewModel
        viewModel.versiAplikasi.value = "Versi Aplikasi ${BuildConfig.VERSION_NAME}"
    }
}