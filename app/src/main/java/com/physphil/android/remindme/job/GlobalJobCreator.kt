package com.physphil.android.remindme.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.physphil.android.remindme.TAG_SHOW_NOTIFICATION_JOB
import com.physphil.android.remindme.data.ReminderRepo

/**
 * A JobCreater subclass to be used with the Job library
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class GlobalJobCreator(private val repo: ReminderRepo) : JobCreator {
    override fun create(tag: String): Job? {
        return when (tag) {
            TAG_SHOW_NOTIFICATION_JOB -> ShowNotificationJob(repo)
            else -> null
        }
    }
}