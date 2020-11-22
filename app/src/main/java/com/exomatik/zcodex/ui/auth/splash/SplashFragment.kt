package com.exomatik.zcodex.ui.auth.splash

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentSplashBinding

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