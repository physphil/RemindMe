package com.physphil.android.remindme.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.room.entities.NEW_REMINDER_ID
import com.physphil.android.remindme.room.entities.Reminder
import java.util.*

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderViewModel(id: Int, private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModel() {

    private val reminder = repo.getReminderById(id)
    private val reminderTime = MutableLiveData<String>()
    private val reminderDate = MutableLiveData<String>()
    private val reminderRecurrence = MutableLiveData<Int>()

    val toolbarTitle = if (id == NEW_REMINDER_ID) R.string.title_add_reminder else R.string.title_edit_reminder


    fun getReminderValue() = reminder.value!!
    fun getReminder(): LiveData<Reminder> = reminder
    fun getReminderTime(): LiveData<String> = reminderTime
    fun getReminderDate(): LiveData<String> = reminderDate
    fun getReminderRecurrence(): LiveData<Int> = reminderRecurrence

    fun updateTitle(title: String) {
        getReminderValue().title = title
    }

    fun updateBody(body: String) {
        getReminderValue().body = body
    }

    fun updateTime(hourOfDay: Int, minute: Int) {
        getReminderValue().time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        getReminderValue().time.set(Calendar.MINUTE, minute)
        reminderTime.value = getReminderValue().getDisplayTime()
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        getReminderValue().time.set(Calendar.YEAR, year)
        getReminderValue().time.set(Calendar.MONTH, month)
        getReminderValue().time.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        reminderDate.value = getReminderValue().getDisplayDate()
    }

    fun updateRecurrence(recurrence: Recurrence) {
        getReminderValue().recurrence = recurrence
        reminderRecurrence.value = recurrence.getDisplayString()
    }

    fun saveReminder() {
        val reminder = getReminderValue()
        if (reminder.isNewReminder()) {
            getReminderValue().externalId = scheduler.scheduleShowNotificationJob(reminder.time.timeInMillis,
                    reminder.id,
                    reminder.title,
                    reminder.body,
                    reminder.recurrence.id)
            repo.insertReminder(reminder)
        }
        else {
            // TODO - reschedule existing notification based on it's job id
            // Also - cancel
            repo.updateReminder(reminder)
        }
    }
}