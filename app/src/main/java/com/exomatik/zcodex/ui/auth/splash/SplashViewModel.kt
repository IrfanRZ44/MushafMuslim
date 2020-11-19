package com.exomatik.zcodex.ui.auth.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.navigation.NavController
import com.exomatik.zcodex.BuildConfig
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelInfoApps
import com.exomatik.zcodex.ui.main.MainActivity
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.ads.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SplashViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?
    ) : BaseViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.dateFormat1))
    } else {
        SimpleDateFormat(Constant.dateFormat1).format(Date())
    }

    private fun getInfoApps() {
        isShowLoading.value = true

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(result: DatabaseError) {
                isShowLoading.value = false
                message.value = "Gagal mengambil data aplikasi"
            }

            override fun onDataChange(result: DataSnapshot) {
                isShowLoading.value = false
                if (result.exists()) {
                    val data = result.getValue(ModelInfoApps::class.java)

                    savedData?.setDataObject(data, Constant.referenceInfoApps)
                    checkingSavedData()
                }
                else{
                    message.value = "Terjadi kesalahan database"
                }
            }
        }

        FirebaseUtils.getDataObject(
            Constant.referenceInfoApps
            , valueEventListener
        )
    }

    private fun setUpIntersitialAds(){
        MobileAds.initialize(activity) {}

        val mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = Constant.defaultIntersitialID
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdOpened() {
            }

            override fun onAdClicked() {
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClosed() {
            }
        }
    }

    fun checkingSavedData() {
        val dataApps = savedData?.getDataApps()

        if (dataApps == null){
            getInfoApps()
        }
        else{
            if (dataApps.versionApps == BuildConfig.VERSION_NAME){
                if (dataApps.statusApps == Constant.active){
                    timerNavigation()
                }
                else{
                    message.value = "Aplikasi sedang dalam perbaikan, mohon tunggu..."
                }
            }
            else{
                message.value = "Mohon update versi aplikasi ${dataApps.versionApps}"
            }
        }
    }

    private fun timerNavigation(){
        isShowLoading.value = true

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (savedData?.getDataUser()?.noHp.isNullOrEmpty()){
                    navController.navigate(R.id.loginFragment)
                }
                else{
                    isShowLoading.value = false
                    setUpIntersitialAds()

                    if (savedData?.getKeyString(Constant.adsDate) != tglSekarang){
                        savedData?.setDataString(tglSekarang, Constant.adsDate)
                        savedData?.setDataInt(savedData.getDataApps()?.totalAds, Constant.adsLeft)
                    }

                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            }
        }.start()
    }
}