package com.physphil.android.remindme.reminders.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.room.entities.Reminder

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListViewModel(private val repo: ReminderRepo) : ViewModel() {

    private val reminderList = repo.getActiveReminders()

    fun getReminderList(): LiveData<List<Reminder>> = reminderList
}