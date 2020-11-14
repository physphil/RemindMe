package com.physphil.android.remindme.job

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.evernote.android.job.Job
import com.physphil.android.remindme.CHANNEL_NOTIFICATIONS
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.models.SnoozeDuration
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.util.Notification
import com.physphil.android.remindme.util.localDateTimeFromMillis
import org.threeten.bp.LocalDateTime

/**
 * Copyright (c) 2017 Phil Shadlyn
 *
 * Subclass of Job to display a notification to the user
 */
class ShowNotificationJob(private val repo: ReminderRepo) : Job() {

    override fun onRunJob(params: Params): Result {
        // Only continue if the notification being shown has a valid id attached to it
        if (params.extras.containsKey(EXTRA_ID)) {

            val id = params.extras.getString(EXTRA_ID, "should never happen")
            val title = params.extras.getString(EXTRA_TITLE, "")
            val text = params.extras.getString(EXTRA_TEXT, "")

            // Create backstack when opening Reminder details, after user clicks on notification
            // PendingIntent requires a unique ID, so that the notifications can be cleared individually by ID
            val notificationId = Notification.nextId
            val intent = ReminderActivity.intent(context, id)
            val pi = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT)

            // Show notification to user
            val builder = NotificationCompat.Builder(context, CHANNEL_NOTIFICATIONS)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pi)
                    .setContentTitle(title)
                    .setColor(ContextCompat.getColor(context, R.color.colorNotifications))
                    .addAction(R.drawable.ic_clock_silver_blue_24dp,
                            context.getString(R.string.snooze_1_hour),
                            getSnoozePendingIntent(SnoozeDuration.OneHour, notificationId, title, text))
                    .addAction(R.drawable.ic_clock_silver_blue_24dp,
                            context.getString(R.string.snooze_3_hours),
                            getSnoozePendingIntent(SnoozeDuration.ThreeHours, notificationId, title, text))
                    .addAction(R.drawable.ic_clock_silver_blue_24dp,
                            context.getString(R.string.snooze_tomorrow),
                            getSnoozePendingIntent(SnoozeDuration.Tomorrow, notificationId, title, text))

            if (text.isNotEmpty()) {
                builder.setContentText(text)
                builder.setStyle(NotificationCompat.BigTextStyle().setSummaryText(text))
            }

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, builder.build())
            repo.updateNotificationId(id, notificationId)

            // Schedule the next event in the series if the Reminder has a recurrence
            val recurrence = Recurrence.fromId(params.extras.getInt(EXTRA_RECURRENCE, Recurrence.NONE.id))
            if (recurrence != Recurrence.NONE) {
                val millis = params.extras.getLong(EXTRA_TIME, System.currentTimeMillis())
                val reminder = Reminder(
                    id = id,
                    title = title,
                    body = text,
                    recurrence = recurrence,
                    time = localDateTimeFromMillis(millis).nextScheduledTime(recurrence)
                )
                repo.updateRecurringReminder(reminder)
            }
            return Result.SUCCESS
        }
        else {
            return Result.FAILURE
        }
    }

    private fun getSnoozePendingIntent(snooze: SnoozeDuration, notificationId: Int, title: String, text: String): PendingIntent {
        val intent = Intent(context, SnoozeBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_SNOOZE_DURATION, snooze)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_TEXT, text)
        }

        // Request codes must be unique in order to create unique PendingIntents
        return PendingIntent.getBroadcast(context, Notification.nextId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun LocalDateTime.nextScheduledTime(recurrence: Recurrence): LocalDateTime =
        when (recurrence) {
            Recurrence.HOURLY -> this.plusHours(1)
            Recurrence.DAILY -> this.plusDays(1)
            Recurrence.WEEKLY -> this.plusWeeks(1)
            Recurrence.MONTHLY -> this.plusMonths(1)
            Recurrence.YEARLY -> this.plusYears(1)
            Recurrence.NONE -> this
        }
}