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

    val reminderListRx = repo.getActiveRemindersRx()
    val clearNotificationEvent = SingleLiveEvent<Int?>()
    val showDeleteConfirmationEvent = SingleLiveEvent<Void>()
    val showDeleteAllConfirmationEvent = SingleLiveEvent<Void>()
    private val spinnerVisibility = MutableLiveData<Boolean>()
    private val emptyVisibility = MutableLiveData<Boolean>()

    private var reminderToDelete: Reminder? = null

    init {
        spinnerVisibility.value = true
        emptyVisibility.value = false
    }

    fun getSpinnerVisibility(): LiveData<Boolean> = spinnerVisibility
    fun getEmptyVisibility(): LiveData<Boolean> = emptyVisibility

    fun reminderListUpdated(reminders: List<Reminder>) {
        spinnerVisibility.value = false
        emptyVisibility.value = reminders.isEmpty()
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