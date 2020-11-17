package com.exomatik.zcodex.ui.auth.splash

import android.content.Intent
import android.os.Handler
import androidx.navigation.fragment.findNavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragment
import com.exomatik.zcodex.ui.main.MainActivity
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.showLog
import com.google.android.gms.ads.*

class SplashFragment : BaseFragment() {
    override fun getLayoutResource(): Int = R.layout.fragment_splash
    private lateinit var mInterstitialAd: InterstitialAd

    override fun myCodeHere() {
        Handler().postDelayed({
            if (savedData.getDataUser()?.noHp.isNullOrEmpty()){
                findNavController().navigate(R.id.loginFragment)
            }
            else{
                setAdMobIntersitial()
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }, 2000L)
    }

    private fun setAdMobIntersitial(){
        MobileAds.initialize(activity) {}

        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = Constant.defaultIntersitialID
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    showLog("Ads is not loaded yet")
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                showLog(adError.toString())
            }

            override fun onAdOpened() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }

            override fun onAdClicked() {
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClosed() {
            }
        }
    }
}