package com.exomatik.zcodex.ui.auth

import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BaseActivity() {
    override fun getLayoutResource(): Int = R.layout.activity_auth

    override fun myCodeHere() {
        NavHostFragment.create(R.navigation.auth_nav)
        viewParent.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
