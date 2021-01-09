package id.exomatik.mushafmuslim.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.exomatik.mushafmuslim.R

class BackgroundService : BroadcastReceiver() {

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        val savedData = DataSave(context)
        val randomTimer = intent.getLongExtra(Constant.randomTimer, 300000)

        val builder = NotificationCompat.Builder(context, "notify${Constant.appName}")
        builder.setSmallIcon(R.drawable.ic_notif)
        builder.setContentTitle(Constant.appName)
        builder.setContentText("Poin berikutnya sudah tersedia")
        builder.priority = NotificationCompat.PRIORITY_DEFAULT

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Poin berikutnya sudah tersedia")
        bigText.setBigContentTitle(Constant.appName)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(uri)
        builder.setLights(Color.BLUE, 500, 500)
        builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, builder.build())

    }
}