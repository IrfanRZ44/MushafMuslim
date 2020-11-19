package com.exomatik.zcodex.ui.main.addNotes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.utils.*
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddNotesViewModel(
    private val activity: Activity?,
    private val dataSave: DataSave?,
    private val navController: NavController
) : BaseViewModel() {
    val title = MutableLiveData<String>()
    val notes = MutableLiveData<String>()

    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
    } else {
        SimpleDateFormat(Constant.timeDateFormat).format(Date())
    }

    fun onClickSave(){
        activity?.let { dismissKeyboard(it) }
        val username = dataSave?.getDataUser()?.username

        if (!username.isNullOrEmpty()){
            isShowLoading.value = true
            val dataNotes = ModelNotes("", title.value?:"Untitled $tglSekarang",
                notes.value?:"", tglSekarang, tglSekarang, username)

            val onCompleteListener = OnCompleteListener<Void> { result ->
                isShowLoading.value = false
                if (result.isSuccessful) {
                    setUpIntersitialAds()
                    message.value = "Succed creating notes"
                    navController.navigate(R.id.nav_beranda)
                } else {
                    message.value = "Failed to create notes"
                }
            }

            val onFailureListener = OnFailureListener { result ->
                isShowLoading.value = false
                message.value = result.message
            }

            FirebaseUtils.setValueUniqueNotes(
                Constant.referenceNotes
                , username
                , dataNotes
                , onCompleteListener
                , onFailureListener
            )
        }
        else{
            message.value = "Failed to save notes"
        }
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
}