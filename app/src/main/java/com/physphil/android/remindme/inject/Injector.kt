package com.physphil.android.remindme.inject

import android.content.Context
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.room.AppDatabase

/**
 * Centralized location for providing required common dependencies
 */
object Injector {
    fun provideReminderRepo(context: Context): ReminderRepo =
        ReminderRepo(
            dao = AppDatabase.getInstance(context).reminderDao(),
            scheduler = JobRequestScheduler()
        )

    fun provideJobRequestScheduler(): JobRequestScheduler = JobRequestScheduler()
}