package com.physphil.android.remindme.job

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import com.evernote.android.job.Job
import com.physphil.android.remindme.CHANNEL_NOTIFICATIONS
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.room.AppDatabase
import java.util.*

/**
 * Copyright (c) 2017 Phil Shadlyn
 *
 * Subclass of Job to display a notification to the user
 */
class ShowNotificationJob : Job() {

    override fun onRunJob(params: Params): Result {
        // Only continue if the notification being shown has a valid id attached to it
        if (params.extras.containsKey(EXTRA_ID)) {
            val id = params.extras.getString(EXTRA_ID, "should never happen")
            val title = params.extras.getString(EXTRA_TITLE, "")
            val text = params.extras.getString(EXTRA_TEXT, "")

            // Create backstack when opening Reminder details, after user clicks on notification
            // PendingIntent requires a unique ID, so that the notifications can be cleared individually by ID
            val notificationId = System.currentTimeMillis().toInt()
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

            if (text.isNotEmpty()) {
                builder.setContentText(text)
                builder.setStyle(NotificationCompat.BigTextStyle().setSummaryText(text))
            }

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, builder.build())
            ReminderRepo(AppDatabase.getInstance(context).reminderDao()).updateNotificationId(id, notificationId)

            // Schedule the next event if the Reminder has a recurrence
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
        val newId = JobRequestScheduler.scheduleShowNotificationJob(newTime, id, title, text, recurrence.id)
        ReminderRepo(AppDatabase.getInstance(context).reminderDao()).updateRecurringReminder(id, newId, newTime)
    }
}