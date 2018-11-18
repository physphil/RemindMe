package com.physphil.android.remindme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler

/**
 * Used to create a MainActivityViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModelFactory(private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(repo, scheduler) as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}