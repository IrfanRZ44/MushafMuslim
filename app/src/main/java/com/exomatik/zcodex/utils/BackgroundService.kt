package com.exomatik.zcodex.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.widget.Toast
import com.exomatik.zcodex.services.notification.model.Notification
import com.exomatik.zcodex.services.notification.model.Sender

class BackgroundService : BroadcastReceiver() {

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        val savedData = DataSave(context)
        val randomTimer = intent.getLongExtra(Constant.randomTimer, 300000)
        object : CountDownTimer(randomTimer, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                val notification = Notification("Iklan berikutnya sudah tersedia",
                    "ZCode")

                val sender = Sender(notification, savedData.getDataUser()?.token)
                FirebaseUtils.sendNotif(sender)
            }
        }.start()
    }
}