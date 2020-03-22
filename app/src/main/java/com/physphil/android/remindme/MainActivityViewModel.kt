package com.physphil.android.remindme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.util.SingleLiveEvent

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModel(
    private val repo: ReminderRepo,
    private val scheduler: JobRequestScheduler
) : ViewModel() {

    val reminderList = repo.getActiveReminders()

    private val _clearNotificationEvent = SingleLiveEvent<Delete>()
    val clearNotificationEvent: LiveData<Delete> = _clearNotificationEvent

    private val _showDeleteConfirmationEvent = SingleLiveEvent<Reminder>()
    val showDeleteConfirmationEvent: LiveData<Reminder> = _showDeleteConfirmationEvent

    private val _showDeleteAllConfirmationEvent = SingleLiveEvent<Unit>()
    val showDeleteAllConfirmationEvent: LiveData<Unit> = _showDeleteAllConfirmationEvent

    private val _spinnerVisibilityLiveData = MutableLiveData<Boolean>()
    val spinnerVisibilityLiveData: LiveData<Boolean> = _spinnerVisibilityLiveData

    private val _emptyVisibilityLiveData = MutableLiveData<Boolean>()
    val emptyVisibilityLiveData: LiveData<Boolean> = _emptyVisibilityLiveData

    init {
        _spinnerVisibilityLiveData.postValue(true)
        _emptyVisibilityLiveData.postValue(false)
    }

    fun reminderListUpdated(size: Int) {
        _spinnerVisibilityLiveData.postValue(false)
        _emptyVisibilityLiveData.postValue(size == 0)
    }

    fun confirmDeleteAllReminders() {
        _showDeleteAllConfirmationEvent.postValue(Unit)
    }

    fun deleteAllReminders() {
        scheduler.cancelAllJobs()
        repo.deleteAllReminders()
        _clearNotificationEvent.postValue(Delete.All)
    }

    fun deleteReminder(reminder: Reminder) {
        scheduler.cancelJob(reminder.externalId)
        repo.deleteReminder(reminder)
        _clearNotificationEvent.postValue(Delete.Single(reminder.notificationId))
        _showDeleteConfirmationEvent.postValue(reminder)
    }

    fun undoDeleteReminder(reminder: Reminder) {
        repo.insertReminder(reminder)
    }

    sealed class Delete {
        object All : Delete()
        data class Single(val id: Int) : Delete()
    }

    class Factory(private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(repo, scheduler) as T
            }

            throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
        }
    }
}