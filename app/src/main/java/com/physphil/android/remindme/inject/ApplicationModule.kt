package com.physphil.android.remindme.inject

import android.content.Context
import com.physphil.android.remindme.MainActivityViewModelFactory
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
@Module
class ApplicationModule(private val application: RemindMeApplication) {

    @Provides
    fun applicationContext(): Context = application

    @Provides
    @Singleton
    fun providesJobRequestScheduler(): JobRequestScheduler = JobRequestScheduler

    @Provides
    @Singleton
    fun providesAppDatabase(): AppDatabase = AppDatabase.getInstance(application)

    @Provides
    @Singleton
    fun providesReminderRepo(appDatabase: AppDatabase): ReminderRepo = ReminderRepo(appDatabase.reminderDao())

    @Provides
    fun providesMainActivityViewModelFactory(repo: ReminderRepo, scheduler: JobRequestScheduler) = MainActivityViewModelFactory(repo, scheduler)
}