package com.example.fcmandroidtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ❌ Removed: enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        // ✅ Track notification open if launched from notification
        val notificationId = intent.getStringExtra("notification_id")
        if (!notificationId.isNullOrEmpty()) {
            trackNotificationOpened(notificationId)
        }
    }

    private fun trackNotificationOpened(notificationId: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("notification_id", notificationId)
            .build()

        val request = Request.Builder()
            .url("https://eea0-2401-4900-1c63-1c88-55d9-eda0-7ab5-d2b1.ngrok-free.app/api/track-notification-opened") // Replace with your backend URL
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

    // Optional: Handle permission result if needed
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("PUSH_Android", "Notification permission granted")
            } else {
                Log.d("PUSH_Android", "Notification permission denied")
            }
        }
    }
}
