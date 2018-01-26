package com.physphil.android.remindme.inject

import com.physphil.android.remindme.reminders.ReminderViewModelFactory
import dagger.Component
import javax.inject.Singleton

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(target: ReminderViewModelFactory)
}