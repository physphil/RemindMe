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
import com.physphil.android.remindme.util.SingleLiveEvent
import com.physphil.android.remindme.util.getDisplayDate
import com.physphil.android.remindme.util.getDisplayTime
import io.reactivex.Flowable
import java.util.Calendar

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderViewModel(id: String? = null, private val repo: ReminderRepo, private val scheduler: JobRequestScheduler) : ViewModel() {

    /** Reminder object retrieved from database, so it can be updated later */
    lateinit var reminder: Reminder
    private val isNewReminder = (id == null)

    /** Flowable containing Reminder saved in database */
    val observableReminder: Flowable<Reminder> = repo.getReminderByIdOrNew(id)
    val clearNotificationEvent = SingleLiveEvent<Int>()
    val confirmDeleteEvent = SingleLiveEvent<Void>()
    val closeActivityEvent = SingleLiveEvent<Void>()

    private val reminderTime = MutableLiveData<String>()
    private val reminderDate = MutableLiveData<String>()
    private val reminderRecurrence = MutableLiveData<Int>()
    private val toolbarTitle = MutableLiveData<Int>()

    init {
        toolbarTitle.value = if (isNewReminder) R.string.title_add_reminder else R.string.title_edit_reminder
    }

    fun getReminderTime(): LiveData<String> = reminderTime
    fun getReminderDate(): LiveData<String> = reminderDate
    fun getReminderRecurrence(): LiveData<Int> = reminderRecurrence
    fun getToolbarTitle(): LiveData<Int> = toolbarTitle

    fun updateTitle(title: String) {
        reminder.title = title
    }

    fun updateBody(body: String) {
        reminder.body = body
    }

    fun updateTime(context: Context, hourOfDay: Int, minute: Int) {
        reminder.time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        reminder.time.set(Calendar.MINUTE, minute)
        reminderTime.value = reminder.getDisplayTime(context)
    }

    fun updateDate(context: Context, year: Int, month: Int, dayOfMonth: Int) {
        reminder.time.set(Calendar.YEAR, year)
        reminder.time.set(Calendar.MONTH, month)
        reminder.time.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        reminderDate.value = reminder.getDisplayDate(context)
    }

    fun updateRecurrence(recurrence: Recurrence) {
        reminder.recurrence = recurrence
        reminderRecurrence.value = recurrence.displayString
    }

    fun saveReminder() {
        // Set all second fields to 0 before saving, so alarm happens at exactly the specified time
        reminder.time.set(Calendar.SECOND, 0)
        reminder.time.set(Calendar.MILLISECOND, 0)

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

    fun confirmDeleteReminder() {
        confirmDeleteEvent.call()
    }

    fun deleteReminder() {
        clearNotificationEvent.value = reminder.notificationId
        scheduler.cancelJob(reminder.externalId)
        repo.deleteReminder(reminder)
        closeActivityEvent.call()
    }

    fun prepareOptionsMenuItems(delete: MenuItem) {
        delete.isVisible = !isNewReminder
    }

    /**
     * Schedule a notification for the Reminder
     * @param reminder the observableReminder to schedule a notification for
     * @return the job id of the newly scheduled notification
     */
    private fun scheduleNotification(reminder: Reminder): Int {
        return scheduler.scheduleShowNotificationJob(reminder.time.timeInMillis,
                reminder.id,
                reminder.title,
                reminder.body,
                reminder.recurrence.id)
    }
}