package com.physphil.android.remindme

import android.app.Application
import com.evernote.android.job.JobManager
import com.physphil.android.remindme.inject.ApplicationComponent
import com.physphil.android.remindme.inject.ApplicationModule
import com.physphil.android.remindme.inject.DaggerApplicationComponent
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.job.GlobalJobCreator

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
open class RemindMeApplication : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        JobManager.create(this).addJobCreator(
            GlobalJobCreator(
                repo = Injector.provideReminderRepo(this),
                scheduler = Injector.provideJobRequestScheduler()
            )
        )
    }

    companion object {
        lateinit var instance: RemindMeApplication
    }
}