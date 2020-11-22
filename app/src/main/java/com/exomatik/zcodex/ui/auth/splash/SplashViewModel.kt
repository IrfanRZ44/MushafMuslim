package com.exomatik.zcodex.ui.auth.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.zcodex.BuildConfig
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelInfoApps
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.ui.main.MainActivity
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
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
    val isShowUpdate = MutableLiveData<Boolean>()

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
                    message.value = dataApps.statusApps
                }
            }
            else{
                message.value = "Mohon perbarui versi aplikasi ke ${dataApps.versionApps}"
                isShowUpdate.value = true
            }
        }
    }

    private fun timerNavigation(){
        isShowLoading.value = true

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (savedData?.getDataUser()?.username.isNullOrEmpty() || savedData?.getDataUser()?.noHp.isNullOrEmpty()){
                    navController.navigate(R.id.loginFragment)
                }
                else{
                    isShowLoading.value = false
                    setUpIntersitialAds()
                    val data = savedData?.getDataUser()
                    val userName = data?.username

                    if (savedData?.getDataUser()?.adsDate != tglSekarang && !userName.isNullOrEmpty()){
                        saveAdsLeft(userName, savedData?.getDataApps()?.totalAds?:Constant.defaultMaxAds, data)
                    }
                    else{
                        val intent = Intent(activity, MainActivity::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
                    }

                }
            }
        }.start()
    }

    private fun saveAdsLeft(userName: String, ads: Long, data: ModelUser) {
        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    data.adsLeft = ads
                    saveTglSekarang(userName, tglSekarang, data)
                } else {
                    Toast.makeText(activity, "Error, terjadi kesalahan database", Toast.LENGTH_SHORT).show()
                }
            }

        val onFailureListener = OnFailureListener { result ->
            Toast.makeText(activity, result.message, Toast.LENGTH_SHORT).show()
        }
        FirebaseUtils.setValueWith2ChildLong(
            Constant.referenceUser
            , userName
            , Constant.adsLeft
            , ads
            , onCompleteListener
            , onFailureListener
        )
    }

    private fun saveTglSekarang(userName: String, tglSekarang: String, data: ModelUser) {
        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    data.adsDate = tglSekarang
                    savedData?.setDataObject(data, Constant.referenceUser)
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                } else {
                    Toast.makeText(activity, "Error, terjadi kesalahan database", Toast.LENGTH_SHORT).show()
                }
            }

        val onFailureListener = OnFailureListener { result ->
            Toast.makeText(activity, result.message, Toast.LENGTH_SHORT).show()
        }

        FirebaseUtils.setValueWith2ChildString(
            Constant.referenceUser
            , userName
            , Constant.adsDate
            , tglSekarang
            , onCompleteListener
            , onFailureListener
        )
    }
}