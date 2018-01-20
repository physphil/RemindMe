package com.physphil.android.remindme

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.room.entities.Reminder
import com.physphil.android.remindme.util.SingleLiveEvent

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModel(private val repo: ReminderRepo) : ViewModel() {

    private val reminderList = repo.getActiveReminders()
    private val spinnerVisibility = MutableLiveData<Boolean>()
    private val emptyVisibility = MutableLiveData<Boolean>()
    private val listVisibility = MutableLiveData<Boolean>()
    private val deleteNotificationsEvent = SingleLiveEvent<Void>()
    private val showDeleteConfirmationEvent = SingleLiveEvent<Void>()

    init {
        spinnerVisibility.value = true
        emptyVisibility.value = false
        listVisibility.value = false
    }

    fun getReminderList(): LiveData<List<Reminder>> = reminderList
    fun getSpinnerVisibility(): LiveData<Boolean> = spinnerVisibility
    fun getListVisibility(): LiveData<Boolean> = listVisibility
    fun getEmptyVisibility(): LiveData<Boolean> = emptyVisibility
    fun getDeleteAllNotificationsEvent(): LiveData<Void> = deleteNotificationsEvent
    fun getShowDeleteConfirmationEvent(): LiveData<Void> = showDeleteConfirmationEvent

    fun reminderListUpdated() {
        spinnerVisibility.value = false
        listVisibility.value = reminderList.value?.isNotEmpty() ?: false
        emptyVisibility.value = reminderList.value?.isEmpty() ?: true
    }

    fun confirmDeleteAllReminders() {
        showDeleteConfirmationEvent.call()
    }

    fun deleteAllReminders() {
        repo.deleteAllReminders()
        deleteNotificationsEvent.call()
    }
}