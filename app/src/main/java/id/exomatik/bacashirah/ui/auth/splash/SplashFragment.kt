package id.exomatik.bacashirah.ui.auth.splash

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import id.exomatik.bacashirah.R
import id.exomatik.bacashirah.base.BaseFragmentBind
import id.exomatik.bacashirah.databinding.FragmentSplashBinding

class SplashFragment : BaseFragmentBind<FragmentSplashBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_splash
    lateinit var viewModel: SplashViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = SplashViewModel(findNavController(), savedData, activity)
        bind.viewModel = viewModel
        viewModel.checkingSavedData()

        bind.btnRate.setOnClickListener {
            activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context?.packageName}")
                )
            )
        }
    }
}