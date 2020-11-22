package com.exomatik.zcodex.services.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.exomatik.zcodex.R
import com.exomatik.zcodex.utils.Constant.referenceToken
import com.exomatik.zcodex.utils.Constant.referenceUser
import com.exomatik.zcodex.utils.DataSave
import com.exomatik.zcodex.utils.FirebaseUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        tambahUID(s)
    }

    private fun tambahUID(token: String) {
        val savedData = DataSave(this@MyFirebaseMessaging)
        val dataUser = savedData.getDataUser()
        if (dataUser != null) {
            val onCompleteListener =
                OnCompleteListener<Void> { result ->
                    if (result.isSuccessful) {
                        dataUser.token = token
                        savedData.setDataObject(dataUser, referenceUser)
                        Toast.makeText(this@MyFirebaseMessaging, "Token notifikasi Anda di update secara otomatis", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MyFirebaseMessaging, "Gagal mengupdate token, mohon logout untuk dapat menerima notifikasi", Toast.LENGTH_SHORT).show()
                    }
                }

            val onFailureListener = OnFailureListener {
                Toast.makeText(this@MyFirebaseMessaging, "Gagal mengupdate token, mohon logout untuk dapat menerima notifikasi", Toast.LENGTH_SHORT).show()
            }

            FirebaseUtils.setValueWith2ChildString(
                referenceUser
                , dataUser.username
                , referenceToken
                , token
                , onCompleteListener
                , onFailureListener
            )
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        notif(p0)
    }

    private var mNotificationManager: NotificationManager? = null
//    private fun notif(remoteMessage: RemoteMessage) {
//        val mBuilder = NotificationCompat.Builder(
//            applicationContext, resources.getString(
//                R.string.app_name
//            )
//        )
//
//        val intent = Intent(remoteMessage.notification?.clickAction)
//        intent.action = remoteMessage.notification?.clickAction
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val bigText = NotificationCompat.BigTextStyle()
//        bigText.bigText(remoteMessage.notification?.body)
//        bigText.setBigContentTitle(remoteMessage.notification?.title)
//        mBuilder.setContentIntent(pendingIntent)
//        mBuilder.setSmallIcon(R.drawable.ic_notif)
//        mBuilder.setContentTitle(remoteMessage.notification?.title)
//        mBuilder.setContentText(remoteMessage.notification?.body)
//        mBuilder.setStyle(bigText)
//        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        mBuilder.setSound(uri, AudioManager.STREAM_NOTIFICATION)
//        mBuilder.setAutoCancel(true)
//        mBuilder.setLights(Color.BLUE, 500, 500)
//        mBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
//
//        mBuilder.priority = NotificationCompat.PRIORITY_HIGH
//
//        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = resources.getString(R.string.app_name)
//            val channel = NotificationChannel(
//                channelId,
//                resources.getString(R.string.app_name),
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            channel.lightColor = Color.BLUE
//            channel.enableLights(true)
//            val uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            channel.setSound(uriSound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
//            channel.enableVibration(true)
//            mNotificationManager?.createNotificationChannel(channel)
//            mBuilder.setChannelId(channelId)
//        }
//        mNotificationManager?.notify(1, mBuilder.build())
//    }

    private fun notif(remoteMessage: RemoteMessage) {
        val mBuilder = NotificationCompat.Builder(
            applicationContext, resources.getString(
                R.string.app_name
            )
        )

        val intent = Intent(remoteMessage.notification?.clickAction)
        intent.action = remoteMessage.notification?.clickAction
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(remoteMessage.notification?.body)
        bigText.setBigContentTitle(remoteMessage.notification?.title)
        bigText.setSummaryText(remoteMessage.notification?.body)
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_notif)
        mBuilder.setContentTitle(remoteMessage.notification?.title)
        mBuilder.setContentText(remoteMessage.notification?.body)
        mBuilder.setStyle(bigText)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setSound(uri)
        mBuilder.setAutoCancel(true)
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId: String = resources.getString(R.string.app_name)
            val channel = NotificationChannel(
                channelId,
                resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lightColor = Color.BLUE
            channel.enableLights(true)
            val uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            channel.setSound(uriSound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            channel.enableVibration(true)
            mNotificationManager?.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        mNotificationManager?.notify(0, mBuilder.build())
    }
}