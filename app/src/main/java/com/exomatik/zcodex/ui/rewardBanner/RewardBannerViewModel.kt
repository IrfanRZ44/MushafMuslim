package com.exomatik.zcodex.ui.rewardBanner

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
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
        val dataUser = savedData?.getDataUser()
        val username = savedData?.getDataUser()?.username

        if (dataUser != null && !username.isNullOrEmpty()){
            isShowLoading.value = true
            textStatus.visibility = View.VISIBLE

            val dataTransaction = ModelTransaction("", tglSekarang, username, 1)

            val onCompleteListener = OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    val poin = dataUser.totalPoin + 1

                    addTotalPoin(dataUser, username, poin)
                } else {
                    textStatus.visibility = View.GONE
                    isShowLoading.value = false
                    message.value = "Gagal menambah Poin"
                }
            }

            val onFailureListener = OnFailureListener {
                textStatus.visibility = View.GONE
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
                textStatus.visibility = View.GONE
                isShowLoading.value = false
                message.value = "Gagal menambah Poin"
            }
        }

        val onFailureListener = OnFailureListener {
            textStatus.visibility = View.GONE
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

    private fun saveAdsLeft(dataUser: ModelUser, username: String, ads: Long){
        val onCompleteListener = OnCompleteListener<Void> { result ->
            if (result.isSuccessful) {
                val timeMin = savedData?.getDataApps()?.timeMin?:5
                val timeMax = savedData?.getDataApps()?.timeMax?:10
                val randomTimer = (timeMin..timeMax).random() * 60000

                createAlarmManager(randomTimer, ads, dataUser)
            } else {
                textStatus.visibility = View.GONE
                isShowLoading.value = false
                message.value = "Gagal menambah Poin"
            }
        }

        val onFailureListener = OnFailureListener {
            textStatus.visibility = View.GONE
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

    private fun createAlarmManager(randomTime: Long, ads: Long, dataUser: ModelUser){
        createChannel()

        val backgroundService = Intent(activity, BackgroundService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, backgroundService, 0)
        val taskManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager?

        val timeAtButtonClick = System.currentTimeMillis()

        taskManager?.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + randomTime, pendingIntent)

        itsDone(randomTime, ads, dataUser)
    }

    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val description = "Channel for ${Constant.appName}"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notify${Constant.appName}", description, importance)
            channel.description = description

            channel.lightColor = Color.BLUE
            channel.enableLights(true)
            val uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            channel.setSound(uriSound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            channel.enableVibration(true)

            val notificationManager = activity?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun itsDone(randomTimer: Long, ads: Long, dataUser: ModelUser){
        val date = Calendar.getInstance()
        val time = date.timeInMillis
        val afterAddingTenMins = Date(time + randomTimer)
        val dateAvailable = SimpleDateFormat(Constant.timeDateFormat).format(afterAddingTenMins)
        savedData?.setDataString(dateAvailable.toString(), Constant.adsTimer)

        dataUser.adsLeft = ads
        savedData?.setDataObject(dataUser, Constant.referenceUser)
        savedData?.setDataBoolean(false, Constant.adsAlreadyNote)

        val adsRewardVideo = savedData?.getKeyInt(Constant.adsRewardVideo)?:0
        if (adsRewardVideo <= 0){
            savedData?.setDataBoolean(false, Constant.adsAlreadyVideo)
        }
        else{
            val adsVid = adsRewardVideo - 1
            savedData?.setDataInt(adsVid, Constant.adsRewardVideo)
        }
        Toast.makeText(activity, "Berhasil menambah 1 Poin", Toast.LENGTH_LONG).show()
        isShowLoading.value = false
        textStatus.visibility = View.GONE
        activity?.finish()
    }
}