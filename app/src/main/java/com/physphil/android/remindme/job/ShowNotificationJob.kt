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
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.util.Notification
import java.util.Calendar
import javax.inject.Inject

/**
 * Copyright (c) 2017 Phil Shadlyn
 *
 * Subclass of Job to display a notification to the user
 */
class ShowNotificationJob : Job() {

    @Inject
    lateinit var repo: ReminderRepo

    @Inject
    lateinit var scheduler: JobRequestScheduler

    override fun onRunJob(params: Params): Result {
        // Only continue if the notification being shown has a valid id attached to it
        if (params.extras.containsKey(EXTRA_ID)) {
            RemindMeApplication.instance.applicationComponent.inject(this)

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
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .addAction(R.drawable.ic_clock_purple_24dp,
                            context.getString(R.string.snooze_20_min),
                            getSnoozePendingIntent(SnoozeDuration.TWENTY_MIN, notificationId, title, text))
                    .addAction(R.drawable.ic_clock_purple_24dp,
                            context.getString(R.string.snooze_1_hour),
                            getSnoozePendingIntent(SnoozeDuration.ONE_HOUR, notificationId, title, text))
                    .addAction(R.drawable.ic_clock_purple_24dp,
                            context.getString(R.string.snooze_3_hours),
                            getSnoozePendingIntent(SnoozeDuration.THREE_HOURS, notificationId, title, text))

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
                scheduleNextNotification(params.extras.getLong(EXTRA_TIME, System.currentTimeMillis()), id, title, text, recurrence)
            }
            return Result.SUCCESS
        }
        else {
            return Result.FAILURE
        }
    }

    private fun scheduleNextNotification(time: Long, id: String, title: String, text: String, recurrence: Recurrence) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        when (recurrence) {
            Recurrence.HOURLY -> calendar.add(Calendar.HOUR_OF_DAY, 1)
            Recurrence.DAILY -> calendar.add(Calendar.DATE, 1)
            Recurrence.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            Recurrence.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            Recurrence.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }

        val newTime = calendar.timeInMillis
        val newId = scheduler.scheduleShowNotificationJob(newTime, id, title, text, recurrence.id)
        repo.updateRecurringReminder(id, newId, newTime)
    }

    private fun getSnoozePendingIntent(snooze: SnoozeDuration, notificationId: Int, title: String, text: String): PendingIntent {
        val intent = Intent(context, SnoozeBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_OFFSET, snooze.offset)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_TEXT, text)
        }

        // Request codes must be unique in order to create unique PendingIntents
        return PendingIntent.getBroadcast(context, Notification.nextId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private enum class SnoozeDuration(val offset: Long) {
        TWENTY_MIN(1000 * 60 * 20),
        ONE_HOUR(1000 * 60 * 60),
        THREE_HOURS(1000 * 60 * 60 * 3)
    }
}