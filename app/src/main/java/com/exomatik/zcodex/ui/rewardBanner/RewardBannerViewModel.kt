package com.exomatik.zcodex.ui.rewardBanner

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelTransaction
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.showLog
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RewardBannerViewModel(
    private val savedData: DataSave?,
    private val activity: Activity?,
    private val adView: AdView
) : BaseViewModel() {
    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
    } else {
        SimpleDateFormat(Constant.timeDateFormat).format(Date())
    }

    fun setUpBanner(){
        MobileAds.initialize(activity) {}
        adView.loadAd(AdRequest.Builder().build())

        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                message.value = "Berhasil memuat iklan"
            }

            override fun onAdFailedToLoad(error : LoadAdError) {
                showLog(error.toString())
                message.value = "Gagal memuat iklan"
            }

            override fun onAdOpened() {
            }

            override fun onAdClicked() {
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClosed() {
                saveRewarded()
            }
        }
    }

    private fun saveRewarded(){
        isShowLoading.value = true

        val dataUser = savedData?.getDataUser()
        val username = savedData?.getDataUser()?.username

        if (dataUser != null && !username.isNullOrEmpty()){
            isShowLoading.value = true
            val dataTransaction = ModelTransaction("", tglSekarang, username, 1)

            val onCompleteListener = OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    val Poin = dataUser.totalPoin + 1

                    addTotalPoin(dataUser, username, Poin)
                } else {
                    isShowLoading.value = false
                    message.value = "Gagal menambah Poin"
                }
            }

            val onFailureListener = OnFailureListener {
                isShowLoading.value = false
                message.value = "Gagal menambah Poin"
            }

            FirebaseUtils.setValueUniqueTransaction(
                Constant.referenceTransaction
                , dataTransaction
                , onCompleteListener
                , onFailureListener
            )
        }
        else{
            message.value = "Gagal menambah Poin"
        }
    }

    private fun addTotalPoin(dataUser: ModelUser, username: String, Poin: Long){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                isShowLoading.value = false
                message.value = "Berhasil menambah 1 Poin"
                dataUser.totalPoin = Poin
                savedData?.setDataObject(dataUser, Constant.referenceUser)

                activity?.finish()
            } else {
                isShowLoading.value = false
                message.value = "Gagal menambah Poin"
            }
        }

        val onFailureListener = OnFailureListener {
            isShowLoading.value = false
            message.value = "Gagal menambah Poin"
        }

        FirebaseUtils.setValueWith3ChildInt(
            Constant.referenceUser
            , username
            , Constant.referenceTotalPoin
            , Poin
            , onCompleteListener
            , onFailureListener
        )
    }
}