package com.physphil.android.remindme.reminders.list

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo

/**
 * Used to create a ReminderViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListViewModelFactory(private val repo: ReminderRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderListViewModel::class.java)) {
            return ReminderListViewModel(repo) as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}