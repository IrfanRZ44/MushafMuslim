package com.exomatik.zcodex.ui.auth.splash

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import androidx.navigation.NavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.ui.main.MainActivity
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.showLog
import com.google.android.gms.ads.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SplashViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?
    ) : BaseViewModel() {
    private lateinit var mInterstitialAd: InterstitialAd

    private fun setUpIntersitialAds(){
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

    fun getMaintenanceStatus() {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
            }

            override fun onDataChange(result: DataSnapshot) {
                if (result.exists()) {
                    val data = result.getValue(String::class.java)

                    if (data.toString() == Constant.active){
                        timerNavigation()
                    }
                    else{
                        message.value = "Aplikasi sedang dalam perbaikan, mohon tunggu..."
                    }
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceStatusApps
            , valueEventListener
        )
    }

    private fun timerNavigation(){
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (savedData?.getDataUser()?.noHp.isNullOrEmpty()){
                    navController.navigate(R.id.loginFragment)
                }
                else{
                    setUpIntersitialAds()
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            }
        }.start()
    }
}