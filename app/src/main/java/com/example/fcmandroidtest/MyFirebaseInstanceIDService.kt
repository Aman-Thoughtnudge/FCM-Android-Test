package com.example.fcmandroidtest

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.*
import java.io.IOException

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    companion object {
        const val TAG = "PUSH_Android"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val notificationId = remoteMessage.data["notification_id"]

            // ✅ Check permission before showing notification
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                showNotification(it.title, it.body, remoteMessage.data["image_url"], notificationId)
            } else {
                Log.w(TAG, "Notification permission not granted")
                // Optionally, handle this case (e.g., show a toast or store for later)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String?, message: String?, imageUrl: String?, notificationId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", notificationId)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Optional: show image if available
        if (!imageUrl.isNullOrEmpty()) {
            val bitmap = Glide.with(this).asBitmap().load(imageUrl).submit().get()
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, builder.build())

        // Track notification opened when user taps it
        notificationId?.let {
            trackNotificationOpened(it)
        }
    }

    private fun trackNotificationOpened(notificationId: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("notification_id", notificationId)
            .build()

        val request = Request.Builder()
            .url("https://eea0-2401-4900-1c63-1c88-55d9-eda0-7ab5-d2b1.ngrok-free.app/api/track-notification-opened")  // Replace with your backend URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("PUSH_Android", "Failed to track notification open", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("PUSH_Android", "Successfully tracked notification open")
            }
        })
    }
}
