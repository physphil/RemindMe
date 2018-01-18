package com.physphil.android.remindme

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.room.entities.Reminder

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModel(repo: ReminderRepo) : ViewModel() {

    private val reminderList = repo.getActiveReminders()
    private val spinnerVisibility = MutableLiveData<Boolean>()
    private val emptyVisibility = MutableLiveData<Boolean>()
    private val listVisibility = MutableLiveData<Boolean>()

    init {
        spinnerVisibility.value = true
        emptyVisibility.value = false
        listVisibility.value = false
    }

    fun getReminderList(): LiveData<List<Reminder>> = reminderList
    fun getSpinnerVisibility(): LiveData<Boolean> = spinnerVisibility
    fun getListVisibility(): LiveData<Boolean> = listVisibility
    fun getEmptyVisibility(): LiveData<Boolean> = emptyVisibility

    fun reminderListUpdated() {
        spinnerVisibility.value = false
        listVisibility.value = reminderList.value?.isNotEmpty() ?: false
        emptyVisibility.value = reminderList.value?.isEmpty() ?: true
    }
}