package com.exomatik.zcodex.ui.auth.splash

import android.content.Intent
import android.os.Handler
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragment
import com.exomatik.zcodex.ui.main.MainActivity

class SplashFragment : BaseFragment() {
    override fun getLayoutResource(): Int = R.layout.fragment_splash

    override fun myCodeHere() {
        Handler().postDelayed({
            if (savedData.getDataUser()?.noHp.isNullOrEmpty()){
                findNavController().navigate(R.id.loginFragment)
            }
            else{
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }, 2000L)
    }
}