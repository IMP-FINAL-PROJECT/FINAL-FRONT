package com.imp.fluffymood.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.imp.presentation.R
import com.imp.presentation.view.splash.ActSplash
import com.imp.presentation.widget.utils.CommonUtil
import com.imp.presentation.widget.utils.PreferencesUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FireBaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        CommonUtil.log("---- Push message received ---- ")

        var title = ""
        var body = ""

        if (remoteMessage.data.isNotEmpty()) {

            title = remoteMessage.data["title"] ?: ""
            body = remoteMessage.data["body"] ?: ""
        }

        // Screen wake up
        FirebaseUtils.acquireWakeLock(applicationContext)

        CommonUtil.log("title : $title, body : $body")

        val id = createID()
        val intent = Intent(this, ActSplash::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = createNotificationChannel(notificationManager, getString(R.string.app_name), createID().toString())

        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            NotificationCompat.Builder(this, channelId)
        } else {
            NotificationCompat.Builder(this).setPriority(Notification.PRIORITY_DEFAULT)
        }

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder.apply {

            setDefaults(Notification.DEFAULT_ALL)
            setAutoCancel(true)
            setVibrate(longArrayOf(0L))
            setPriority(NotificationCompat.PRIORITY_HIGH)
//            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(body)
            setContentIntent(pendingIntent)
            setSound(sound)
        }

        notificationManager.notify(id, builder.build())
    }

    /**
     * Create Notification Channel
     *
     * @param channelId
     * @param channelName
     */
    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String, channelName: String): String {

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {

            setShowBadge(false)
            lightColor = Color.BLACK
//            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CommonUtil.log("onNewToken : $token")
        PreferencesUtil.setPreferencesString(applicationContext, PreferencesUtil.PUSH_TOKEN, token)
    }

    private fun createID(): Int {

        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("ddHHmmss"))
            .toInt()
    }
}