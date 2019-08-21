package com.physphil.android.remindme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private var reminderToDelete: Reminder? = null

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

    fun confirmDeleteReminder(reminder: Reminder) {
        reminderToDelete = reminder
        _showDeleteConfirmationEvent.postValue(reminder)
    }

    fun deleteReminder() {
        reminderToDelete?.let {
            scheduler.cancelJob(it.externalId)
            repo.deleteReminder(it)
            _clearNotificationEvent.postValue(Delete.Single(it.notificationId))
            reminderToDelete = null
        }
    }

    fun cancelDeleteReminder() {
        reminderToDelete = null
    }

    sealed class Delete {
        object All : Delete()
        data class Single(val id: Int) : Delete()
    }
}