package com.physphil.android.remindme.reminders

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo

/**
 * Used to create a ReminderViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderViewModelFactory(private val application: RemindMeApplication, private val id: String?, private val repo: ReminderRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val viewModel = ReminderViewModel(id, repo)
            application.applicationComponent.inject(viewModel)
            return viewModel as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}