package id.exomatik.bacashirah.ui.main.account

import androidx.navigation.fragment.findNavController
import id.exomatik.bacashirah.BuildConfig
import id.exomatik.bacashirah.R
import id.exomatik.bacashirah.base.BaseFragmentBind
import id.exomatik.bacashirah.databinding.FragmentAccountBinding

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