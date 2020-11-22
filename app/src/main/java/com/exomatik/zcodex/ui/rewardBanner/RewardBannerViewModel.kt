package com.exomatik.zcodex.ui.rewardBanner

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.base.BaseViewModel
import com.exomatik.zcodex.model.ModelTransaction
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.*
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
    private val adView: AdView,
    private val rcVideo: RecyclerView,
    private val textStatus: AppCompatTextView,
    private val context: Context?
) : BaseViewModel() {
    @SuppressLint("SimpleDateFormat")
    private val tglSekarang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constant.timeDateFormat))
    } else {
        SimpleDateFormat(Constant.timeDateFormat).format(Date())
    }

    fun initAdapter(){
        val listVideo = savedData?.getDataApps()?.data_youtube

        if (listVideo != null && context != null){
            textStatus.visibility = View.GONE
            rcVideo.visibility = View.VISIBLE
            rcVideo.setHasFixedSize(true)
            rcVideo.layoutManager = LinearLayoutManager(context)
            rcVideo.adapter = AdapterVideoYoutube(context, listVideo)
        }
        else{
            textStatus.visibility = View.VISIBLE
            rcVideo.visibility = View.GONE
        }
    }

    fun setUpBanner(){
        MobileAds.initialize(activity) {}
        adView.loadAd(AdRequest.Builder().build())

        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                message.value = "Berhasil memuat iklan"
            }

            override fun onAdFailedToLoad(error : LoadAdError) {
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
                    val poin = dataUser.totalPoin + 1

                    addTotalPoin(dataUser, username, poin)
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
                var ads = dataUser.adsLeft
                dataUser.totalPoin = Poin
                ads -= 1
                saveAdsLeft(dataUser, username, ads)
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

    @SuppressLint("SimpleDateFormat")
    private fun saveAdsLeft(dataUser: ModelUser, username: String, ads: Long){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                dataUser.adsLeft = ads
                savedData?.setDataObject(dataUser, Constant.referenceUser)
                savedData?.setDataBoolean(false, Constant.adsAlreadyNote)
                savedData?.setDataBoolean(false, Constant.adsAlreadyVideo)

                val timeMin = savedData?.getDataApps()?.timeMin?:5
                val timeMax = savedData?.getDataApps()?.timeMax?:10
                val randomTimer = (timeMin..timeMax).random() * 60000

                val date = Calendar.getInstance()
                val time = date.timeInMillis
                val afterAddingTenMins = Date(time + randomTimer)
                val dateAvailable = SimpleDateFormat(Constant.timeFormat).format(afterAddingTenMins)
                savedData?.setDataString(dateAvailable.toString(), Constant.adsTimer)

                val taskManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager?
                val backgroundService = Intent(activity, BackgroundService::class.java)
                backgroundService.putExtra(Constant.randomTimer, randomTimer)
                val pendingIntent = PendingIntent.getBroadcast(activity, 0, backgroundService, PendingIntent.FLAG_UPDATE_CURRENT)

                taskManager?.set(AlarmManager.RTC_WAKEUP, 10000, pendingIntent)
                object : CountDownTimer(10000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }
                    override fun onFinish() {
                        Toast.makeText(activity, "Berhasil menambah 1 Poin", Toast.LENGTH_LONG).show()
                        isShowLoading.value = false
                        activity?.finish()
                    }
                }.start()
            } else {
                isShowLoading.value = false
                message.value = "Gagal menambah Poin"
            }
        }

        val onFailureListener = OnFailureListener {
            isShowLoading.value = false
            message.value = "Gagal menambah Poin"
        }

        FirebaseUtils.setValueWith2ChildLong(
            Constant.referenceUser
            , username
            , Constant.adsLeft
            , ads
            , onCompleteListener
            , onFailureListener
        )
    }
}