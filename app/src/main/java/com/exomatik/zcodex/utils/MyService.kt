package com.exomatik.zcodex.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.Nullable
import com.exomatik.zcodex.services.notification.model.Notification
import com.exomatik.zcodex.services.notification.model.Sender
import com.exomatik.zcodex.ui.rewardBanner.RewardBannerActivity
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
        if (enableAds){
            setUpRewardedAds()
            val intent = Intent(this, RewardBannerActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        token = intent.getStringExtra(Constant.referenceToken)

        if (!enableAds){
            Toast.makeText(this, "Mohon tunggu $timerCountDown detik", Toast.LENGTH_SHORT).show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!enableAds){
            val restartServiceIntent = Intent(applicationContext, this.javaClass)
            restartServiceIntent.setPackage(packageName)

            val restartServicePendingIntent = PendingIntent.getService(
                applicationContext,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val alarmService =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
                restartServicePendingIntent
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        if (enableAds){
            Toast.makeText(this, "Iklan berikutnya sudah tersedia", Toast.LENGTH_SHORT).show()
            val notification = Notification("Iklan berikutnya sudah tersedia",
                "ZCode"
                , "com.exomatik.zcode.fcm_TARGET_SPLASH")

            val sender = Sender(notification, token)
            FirebaseUtils.sendNotif(sender)
        }
        stopSelf()
    }

    private fun setUpRewardedAds(){
        val randomTimer = (300000..600000).random().toLong()

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