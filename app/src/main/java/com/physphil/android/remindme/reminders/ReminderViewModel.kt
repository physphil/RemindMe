package com.physphil.android.remindme.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.view.MenuItem
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.room.entities.Reminder
import java.util.*

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderViewModel(id: String?, private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModel() {

    private val isNewReminder = (id == null)

    private val reminder = repo.getReminderById(id)
    private val reminderTime = MutableLiveData<String>()
    private val reminderDate = MutableLiveData<String>()
    private val reminderRecurrence = MutableLiveData<Int>()
    private val toolbarTitle = MutableLiveData<Int>()

    init {
        toolbarTitle.value = if (isNewReminder) R.string.title_add_reminder else R.string.title_edit_reminder
    }

    fun getReminderValue() = reminder.value!!
    fun getReminder(): LiveData<Reminder> = reminder
    fun getReminderTime(): LiveData<String> = reminderTime
    fun getReminderDate(): LiveData<String> = reminderDate
    fun getReminderRecurrence(): LiveData<Int> = reminderRecurrence
    fun getToolbarTitle(): LiveData<Int> = toolbarTitle

    fun updateTitle(title: String) {
        getReminderValue().title = title
    }

    fun updateBody(body: String) {
        getReminderValue().body = body
    }

    fun updateTime(context: Context, hourOfDay: Int, minute: Int) {
        getReminderValue().time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        getReminderValue().time.set(Calendar.MINUTE, minute)
        reminderTime.value = getReminderValue().getDisplayTime(context)
    }

    fun updateDate(context: Context, year: Int, month: Int, dayOfMonth: Int) {
        getReminderValue().time.set(Calendar.YEAR, year)
        getReminderValue().time.set(Calendar.MONTH, month)
        getReminderValue().time.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        reminderDate.value = getReminderValue().getDisplayDate(context)
    }

    fun updateRecurrence(recurrence: Recurrence) {
        getReminderValue().recurrence = recurrence
        reminderRecurrence.value = recurrence.getDisplayString()
    }

    fun saveReminder() {
        val reminder = getReminderValue()
        if (isNewReminder) {
            reminder.externalId = scheduleNotification(reminder)
            repo.insertReminder(reminder)
        }
        else {
            scheduler.cancelJob(reminder.externalId)
            reminder.externalId = scheduleNotification(reminder)
            repo.updateReminder(reminder)
        }
    }

    fun deleteReminder() {
        scheduler.cancelJob(getReminderValue().externalId)
        repo.deleteReminder(getReminderValue())
    }

    fun prepareOptionsMenuItems(delete: MenuItem) {
        delete.isVisible = !isNewReminder
    }

    private fun scheduleNotification(reminder: Reminder): Int {
        return scheduler.scheduleShowNotificationJob(reminder.time.timeInMillis,
                reminder.id,
                reminder.title,
                reminder.body,
                reminder.recurrence.id)
    }
}