package com.physphil.android.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.OnClick
import com.physphil.android.remindme.reminders.ReminderActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        // Create required notification channel on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_NOTIFICATIONS, getString(R.string.channel_notifications), NotificationManager.IMPORTANCE_HIGH)

            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    @OnClick(R.id.text)
    fun onTextClicked() {
        startActivity(ReminderActivity.intent(this))
    }
}
