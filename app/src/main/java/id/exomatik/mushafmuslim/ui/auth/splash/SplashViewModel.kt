package id.exomatik.mushafmuslim.ui.auth.splash

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import id.exomatik.mushafmuslim.BuildConfig
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.ui.main.MainActivity
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.DataSave
import id.exomatik.mushafmuslim.utils.FirebaseUtils
import com.google.android.gms.ads.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.exomatik.mushafmuslim.base.BaseViewModel
import id.exomatik.mushafmuslim.model.ModelInfoApps

class SplashViewModel(
    private val navController: NavController,
    private val savedData: DataSave?,
    private val activity: Activity?
    ) : BaseViewModel() {
    val isShowUpdate = MutableLiveData<Boolean>()

    fun getInfoApps() {
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
        mInterstitialAd.adUnitId = Constant.idIntersitialTesting
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

                    savedData?.setDataObject(data, Constant.referenceUser)
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            }
        }.start()
    }
}