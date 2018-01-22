package com.physphil.android.remindme

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.room.entities.Reminder
import com.physphil.android.remindme.util.SingleLiveEvent

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModel(private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModel() {

    private val reminderList = repo.getActiveReminders()
    private val spinnerVisibility = MutableLiveData<Boolean>()
    private val emptyVisibility = MutableLiveData<Boolean>()
    private val clearNotificationEvent = SingleLiveEvent<Int?>()
    private val showDeleteConfirmationEvent = SingleLiveEvent<Void>()
    private val showDeleteAllConfirmationEvent = SingleLiveEvent<Void>()
    private var reminderToDelete: Reminder? = null

    init {
        spinnerVisibility.value = true
        emptyVisibility.value = false
    }

    fun getReminderList(): LiveData<List<Reminder>> = reminderList
    fun getSpinnerVisibility(): LiveData<Boolean> = spinnerVisibility
    fun getEmptyVisibility(): LiveData<Boolean> = emptyVisibility
    fun getClearNotificationEvent(): LiveData<Int?> = clearNotificationEvent
    fun getShowDeleteConfirmationEvent(): LiveData<Void> = showDeleteConfirmationEvent
    fun getShowDeleteAllConfirmationEvent(): LiveData<Void> = showDeleteAllConfirmationEvent

    fun reminderListUpdated() {
        spinnerVisibility.value = false
        emptyVisibility.value = reminderList.value?.isEmpty() ?: true
    }

    fun confirmDeleteAllReminders() {
        showDeleteAllConfirmationEvent.call()
    }

    fun deleteAllReminders() {
        scheduler.cancelAllJobs()
        repo.deleteAllReminders()
        clearNotificationEvent.call()
    }

    fun confirmDeleteReminder(reminder: Reminder) {
        reminderToDelete = reminder
        showDeleteConfirmationEvent.call()
    }

    fun deleteReminder() {
        reminderToDelete?.let {
            scheduler.cancelJob(it.externalId)
            repo.deleteReminder(it)
            clearNotificationEvent.value = it.notificationId
            reminderToDelete = null
        }
    }

    fun cancelDeleteReminder() {
        reminderToDelete = null
    }
}