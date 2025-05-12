package com.example.fcmandroidtest

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    companion object {
        const val TAG = "PUSH_Android"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Send the token to your app server if needed
        // sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Handle data payload
            // You can check if a long-running job is required and schedule it
            // Or handle immediately if it's a short task
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Generate your own notifications here if needed
        // sendNotification(remoteMessage)
    }

//    private fun showNotification(title: String?, message: String?, imageUrl: String?, notificationId: String?) {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra("notification_id", notificationId)
//        }
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        val builder = NotificationCompat.Builder(this, "default")
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        // Optional: show image if available
//        if (!imageUrl.isNullOrEmpty()) {
//            val bitmap = Glide.with(this).asBitmap().load(imageUrl).submit().get()
//            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
//        }
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, builder.build())
//    }
}
