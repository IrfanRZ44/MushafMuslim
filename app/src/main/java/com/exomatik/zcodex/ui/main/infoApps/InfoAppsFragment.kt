
package com.exomatik.zcodex.ui.main.infoApps

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.exomatik.zcodex.BuildConfig
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentInfoAppsBinding


class InfoAppsFragment : BaseFragmentBind<FragmentInfoAppsBinding>(){
    override fun getLayoutResource(): Int = R.layout.fragment_info_apps

    @SuppressLint("SetTextI18n")
    override fun myCodeHere() {
        bind.textDev.text = "ZCode Development"
        bind.textVersi.text = "Versi Aplikasi ${BuildConfig.VERSION_NAME}"
        bind.btnLinkTelegram.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/ZCodePoin"))
            startActivity(intent)
        }
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