package com.physphil.android.remindme.job

import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.evernote.android.job.Job
import com.physphil.android.remindme.CHANNEL_NOTIFICATIONS
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.room.AppDatabase
import java.util.*

/**
 * Copyright (c) 2017 Phil Shadlyn
 *
 * Subclass of Job to display a notification to the user
 */
class ShowNotificationJob : Job() {

    override fun onRunJob(params: Params): Result {
        // Show notification to user
        val title = params.extras.getString(EXTRA_TITLE, "")
        val text = params.extras.getString(EXTRA_TEXT, "")
        val notification = NotificationCompat.Builder(context, CHANNEL_NOTIFICATIONS)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notification)

        // Schedule the next event if the Reminder has a recurrence
        val recurrence = Recurrence.fromId(params.extras.getInt(EXTRA_RECURRENCE, Recurrence.NONE.id))
        if (recurrence != Recurrence.NONE) {
            scheduleNextNotification(params.extras.getLong(EXTRA_TIME, System.currentTimeMillis()),
                    params.extras.getString(EXTRA_ID, "should never happen"),
                    title, text, recurrence)
        }

        return Result.SUCCESS
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