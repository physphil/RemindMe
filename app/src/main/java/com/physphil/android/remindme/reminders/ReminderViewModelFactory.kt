package com.physphil.android.remindme.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import javax.inject.Inject

/**
 * Used to create a ReminderViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderViewModelFactory(private val application: RemindMeApplication, private val id: String? = null) : ViewModelProvider.Factory {

    // Do injection here as we require the id when creating the factory, and it is unknown at compile time
    @Inject
    lateinit var scheduler: JobRequestScheduler

    @Inject
    lateinit var repo: ReminderRepo

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            application.applicationComponent.inject(this)
            return ReminderViewModel(id, repo, scheduler) as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}