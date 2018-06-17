package com.physphil.android.remindme.job

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.room.entities.Reminder
import java.util.Calendar
import javax.inject.Inject

class SnoozeBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduler: JobRequestScheduler

    @Inject
    lateinit var repo: ReminderRepo

    override fun onReceive(context: Context, intent: Intent) {
        RemindMeApplication.instance.applicationComponent.inject(this)

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val offset = intent.getLongExtra(EXTRA_OFFSET, 0)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)

        // Add a new reminder for the snoozed notification
        val calendar = Calendar.getInstance().apply {
            timeInMillis += offset
        }
        val snoozedReminder = Reminder(title = title, body = text, time = calendar)
        snoozedReminder.notificationId = scheduler.scheduleShowNotificationJob(snoozedReminder)
        repo.insertReminder(snoozedReminder)

        // Dismiss existing notification
        val nm: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
    }
}
