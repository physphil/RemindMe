package com.physphil.android.remindme.job

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.physphil.android.remindme.TAG_SHOW_NOTIFICATION_JOB
import com.physphil.android.remindme.models.Recurrence

const val EXTRA_ID = "com.physphil.android.remindme.EXTRA_ID"
const val EXTRA_TITLE = "com.physphil.android.remindme.EXTRA_TITLE"
const val EXTRA_TEXT = "com.physphil.android.remindme.EXTRA_TEXT"
const val EXTRA_RECURRENCE = "com.physphil.android.remindme.EXTRA_RECURRENCE"
const val EXTRA_TIME = "com.physphil.android.remindme.EXTRA_TIME"

/**
 * Offset (in ms) for any reminder to be scheduled immediately
 */
private const val DEFAULT_OFFSET = 3000

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
object JobRequestScheduler {
    /**
     * Schedule a ShowNotificationJob to display a notification to the user at the specified time.
     * @param time the time (in ms) when the notification should be shown
     * @param id the id of the Reminder in the local database
     * @param title the title of the notification
     * @param text the body of the notification
     * @param recurrence the id of the notification's recurrence
     * @return the id of the newly scheduled job
     */
    fun scheduleShowNotificationJob(time: Long, id: String, title: String = "", text: String = "", recurrence: Int = Recurrence.NONE.id): Int {
        val extras = PersistableBundleCompat()
        extras.putString(EXTRA_ID, id)
        extras.putLong(EXTRA_TIME, time)
        extras.putString(EXTRA_TITLE, title)
        extras.putString(EXTRA_TEXT, text)
        extras.putInt(EXTRA_RECURRENCE, recurrence)

        return JobRequest.Builder(TAG_SHOW_NOTIFICATION_JOB)
                .setExact(calculateOffset(time))    // requires offset from current time
                .setExtras(extras)
                .build()
                .schedule()
    }

    fun cancelJob(id: Int) = JobManager.instance().cancel(id)

    fun cancelAllJobs() {
        JobManager.instance().cancelAllForTag(TAG_SHOW_NOTIFICATION_JOB)
    }

    /**
     * Calculate the time offset required by the JobRequest. If the time supplied is in the past, this function
     * will return [DEFAULT_OFFSET]. This will allow the reminder to be shown immediately.
     * @param time the time at which the reminder is scheduled
     * @return the offset between the supplied time and the current time
     */
    private fun calculateOffset(time: Long): Long {
        val now = System.currentTimeMillis()
        return if (time > now) {
            time - now
        }
        else {
            DEFAULT_OFFSET.toLong()
        }
    }
}