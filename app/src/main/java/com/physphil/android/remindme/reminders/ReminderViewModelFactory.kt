package com.physphil.android.remindme.reminders

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler

/**
 * Used to create a ReminderViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderViewModelFactory(private val id: Int, private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(id, repo, scheduler) as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}