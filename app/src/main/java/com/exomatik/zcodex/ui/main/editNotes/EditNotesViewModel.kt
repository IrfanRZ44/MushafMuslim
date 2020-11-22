package com.exomatik.zcodex.ui.main.editNotes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelNotes
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.dismissKeyboard
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditNotesViewModel(
    private val activity: Activity?,
    private val dataSave: DataSave?,
    private val navController: NavController,
    private val dataNotes: ModelNotes?
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
        val idNotes = dataNotes?.idNotes

        if (!username.isNullOrEmpty() && dataNotes != null && !idNotes.isNullOrEmpty()){
            isShowLoading.value = true
            dataNotes.title = title.value?:"Untitled $tglSekarang"
            dataNotes.notes = notes.value?:""
            dataNotes.tglDiedit = tglSekarang

            val onCompleteListener = OnCompleteListener<Void> { result ->
                isShowLoading.value = false
                if (result.isSuccessful) {
                    setUpIntersitialAds()
                    Toast.makeText(activity, "Berhasil mengubah catatan", Toast.LENGTH_LONG).show()
                    dataSave?.setDataBoolean(true, Constant.adsAlreadyNote)
                    navController.navigate(R.id.nav_beranda)
                } else {
                    message.value = "Gagal mengubah catatan"
                }
            }

            val onFailureListener = OnFailureListener { result ->
                isShowLoading.value = false
                message.value = result.message
            }

            FirebaseUtils.setValueWith2ChildObject(
                Constant.referenceNotes
                , username
                , idNotes
                , dataNotes
                , onCompleteListener
                , onFailureListener
            )
        }
        else{
            message.value = "Failed to edit notes"
        }
    }

    fun setData(data: ModelNotes){
        title.value = data.title
        notes.value = data.notes
    }

    private fun setUpIntersitialAds(){
        MobileAds.initialize(activity) {}

        val mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = Constant.defaultIntersitialIDEditNotes
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