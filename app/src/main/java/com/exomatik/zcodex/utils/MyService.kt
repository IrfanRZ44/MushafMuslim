package com.exomatik.zcodex.utils

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.Nullable
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.services.notification.model.Notification
import com.exomatik.zcodex.services.notification.model.Sender
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import java.util.concurrent.TimeUnit

class MyService : Service() {
    private lateinit var time : CountDownTimer
    private var timerCountDown = 0
    private var enableAds = true
    private var token: String? = ""

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        val savedData = DataSave(this)
        val dataUser = savedData.getDataUser()
        token = dataUser?.token
        var ads = dataUser?.adsLeft?:0
        val username = dataUser?.username

        if (enableAds && !username.isNullOrEmpty()){
            ads -= 1
            saveAdsLeft(username, ads, dataUser)
        }
    }

    private fun saveAdsLeft(userName: String, ads: Long, dataUser: ModelUser) {
        val onCompleteListener =
            OnCompleteListener<Void> { result ->
                if (result.isSuccessful) {
                    setUpTimer(dataUser, ads)
                } else {
                    Toast.makeText(this, "Error, terjadi kesalahan database", Toast.LENGTH_SHORT).show()
                }
            }

        val onFailureListener = OnFailureListener { result ->
            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!enableAds){
            Toast.makeText(this, "Mohon tunggu $timerCountDown detik", Toast.LENGTH_SHORT).show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (enableAds){

        }
        stopSelf()
    }

    private fun setUpTimer(dataUser: ModelUser, ads: Long){
        val randomTimer = (300000..600000).random().toLong()
        val savedData = DataSave(this)
        dataUser.adsLeft = ads
        savedData.setDataObject(dataUser, Constant.referenceUser)

        time = object : CountDownTimer(randomTimer, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerCountDown = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toInt()
                enableAds = false
            }

            override fun onFinish() {
                enableAds = true
                onDestroy()
            }
        }.start()
    }
}