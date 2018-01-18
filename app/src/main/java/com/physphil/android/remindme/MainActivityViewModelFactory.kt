package com.physphil.android.remindme

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo

/**
 * Used to create a MainActivityViewModel with the correct arguments
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModelFactory(private val repo: ReminderRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(repo) as T
        }

        throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
    }
}