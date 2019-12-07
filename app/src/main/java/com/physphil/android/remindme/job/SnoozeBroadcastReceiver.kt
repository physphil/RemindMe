package com.physphil.android.remindme.job

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.models.SnoozeDuration
import com.physphil.android.remindme.util.tomorrowMorning
import org.threeten.bp.LocalDateTime

/**
 * A [BroadcastReceiver] implementation that is run when a user selects a snooze duration for a
 * displayed [Reminder].
 */
class SnoozeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repo = Injector.provideReminderRepo(context)
        val scheduler = Injector.provideJobRequestScheduler()

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val snoozeDuration: SnoozeDuration = intent.getSerializableExtra(EXTRA_SNOOZE_DURATION) as SnoozeDuration
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)

        // Add a new reminder for the snoozed notification
        val reminder = Reminder(
            title = title,
            body = text,
            time = LocalDateTime.now().snooze(snoozeDuration)
        )
        val snoozedReminder = reminder.copy(externalId = scheduler.scheduleShowNotificationJob(reminder))
        repo.insertReminder(snoozedReminder)

        // Dismiss existing notification
        val nm: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
    }

    private fun LocalDateTime.snooze(snoozeDuration: SnoozeDuration): LocalDateTime =
        when (snoozeDuration) {
            SnoozeDuration.OneHour -> plusHours(1)
            SnoozeDuration.ThreeHours -> plusHours(3)
            SnoozeDuration.Tomorrow -> tomorrowMorning()
        }
}
