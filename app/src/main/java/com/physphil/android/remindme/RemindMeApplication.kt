package com.physphil.android.remindme

import android.app.Application
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.job.GlobalJobCreator

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
open class RemindMeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        JobManager.create(this).addJobCreator(
            GlobalJobCreator(
                repo = Injector.provideReminderRepo(this),
                scheduler = Injector.provideJobRequestScheduler()
            )
        )
    }
}