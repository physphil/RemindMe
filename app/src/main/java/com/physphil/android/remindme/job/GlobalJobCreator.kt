package com.physphil.android.remindme.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.physphil.android.remindme.TAG_SHOW_NOTIFICATION_JOB

/**
 * A JobCreater subclass to be used with the Job library
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class GlobalJobCreator : JobCreator {
    override fun create(tag: String): Job? {
        return when (tag) {
            TAG_SHOW_NOTIFICATION_JOB -> ShowNotificationJob()
            else -> null
        }
    }
}