package com.physphil.android.remindme.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo

class StatsViewModel(private val repo: ReminderRepo) : ViewModel() {

    val reminderCountLiveData = repo.getOldReminderCount()

    class Factory(private val repo: ReminderRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                return StatsViewModel(repo) as T
            }

            throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
        }
    }
}