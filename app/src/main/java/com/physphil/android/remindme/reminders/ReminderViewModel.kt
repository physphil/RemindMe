package com.physphil.android.remindme.reminders

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.PresetTime
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.util.SingleLiveEvent
import com.physphil.android.remindme.util.ViewString
import com.physphil.android.remindme.util.isNow
import com.physphil.android.remindme.util.isToday
import com.physphil.android.remindme.util.isTomorrow
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderViewModel(
    id: String? = null,
    time: PresetTime? = null,
    private val repo: ReminderRepo,
    private val scheduler: JobRequestScheduler
) : ViewModel() {

    private val isNewReminder = (id == null)

    // Internal variable to keep track of Reminder state. LiveData should be updated whenever this
    // value changes, so it can provide the proper value on a config change.
    private lateinit var reminder: Reminder

    val reminderLiveData: LiveData<ReminderViewState> = Transformations.map(repo.getReminderByIdOrNew(id, time)) { reminder ->
        this.reminder = reminder
        reminder.toViewState()
    }

    val clearNotificationEvent = SingleLiveEvent<Int>()
    val confirmDeleteEvent = SingleLiveEvent<Void>()
    val closeActivityEvent = SingleLiveEvent<Void>()
    val openTimePickerEvent = SingleLiveEvent<Time>()
    val openDatePickerEvent = SingleLiveEvent<Date>()
    val openRecurrencePickerEvent = SingleLiveEvent<Recurrence>()

    // FIXME add clearNotificationEvent(id)

    private val _reminderTimeLiveData = MutableLiveData<ViewString>()
    fun getReminderTime(): LiveData<ViewString> = _reminderTimeLiveData

    private val _reminderDateLiveData = MutableLiveData<ViewString>()
    fun getReminderDate(): LiveData<ViewString> = _reminderDateLiveData

    private val _reminderRecurrenceLiveData = MutableLiveData<Int>()
    fun getReminderRecurrence(): LiveData<Int> = _reminderRecurrenceLiveData

    private val _toolbarTitleLiveData = MutableLiveData<Int>().apply {
        postValue(if (isNewReminder) R.string.title_add_reminder else R.string.title_edit_reminder)
    }
    fun getToolbarTitle(): LiveData<Int> = _toolbarTitleLiveData

    fun updateTitle(title: String) {
        reminder = reminder.copy(title = title)
    }

    fun updateBody(body: String) {
        reminder = reminder.copy(body = body)
    }

    fun updateTime(hourOfDay: Int, minute: Int) {
        reminder.time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        reminder.time.set(Calendar.MINUTE, minute)
        _reminderTimeLiveData.postValue(reminder.displayTime)
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        reminder.time.set(Calendar.YEAR, year)
        reminder.time.set(Calendar.MONTH, month)
        reminder.time.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        _reminderDateLiveData.postValue(reminder.displayDate)
    }

    fun updateRecurrence(recurrence: Recurrence) {
        reminder = reminder.copy(recurrence = recurrence)
        _reminderRecurrenceLiveData.postValue(recurrence.displayString)
    }

    fun openTimePicker() {
        with(reminder.time) {
            openTimePickerEvent.postValue(Time(
                    hour = get(Calendar.HOUR_OF_DAY),
                    minute = get(Calendar.MINUTE)
            ))
        }
    }

    fun openDatePicker() {
        with(reminder.time) {
            openDatePickerEvent.postValue(Date(
                    year = get(Calendar.YEAR),
                    month = get(Calendar.MONTH),
                    day = get(Calendar.DAY_OF_MONTH)
            ))
        }
    }

    fun openRecurrencePicker() {
        openRecurrencePickerEvent.postValue(reminder.recurrence)
    }

    fun saveReminder() {
        // Set all second fields to 0 before saving, so alarm happens at exactly the specified time
        reminder.time.set(Calendar.SECOND, 0)
        reminder.time.set(Calendar.MILLISECOND, 0)

        if (isNewReminder) {
            reminder = reminder.copy(externalId = scheduleNotification(reminder))
            repo.insertReminder(reminder)
        }
        else {
            scheduler.cancelJob(reminder.externalId)
            reminder = reminder.copy(externalId = scheduleNotification(reminder))
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

    private fun Reminder.toViewState(): ReminderViewState = ReminderViewState(
        title = title,
        body = body,
        time = displayTime,
        date = displayDate,
        recurrence = recurrence.displayString
    )

    private val Reminder.displayTime: ViewString
        get() = when {
            time.isNow() -> ViewString.Integer(R.string.reminder_time_now)
            else -> ViewString.String(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(time.time))
        }

    private val Reminder.displayDate: ViewString
        @SuppressLint("SimpleDateFormat")
        get() = when {
            time.isToday() -> ViewString.Integer(R.string.reminder_repeat_today)
            time.isTomorrow() -> ViewString.Integer(R.string.reminder_repeat_tomorrow)
            else -> ViewString.String(SimpleDateFormat("EEE MMM d, yyyy").format(time.time))
        }

    data class Time(val hour: Int, val minute: Int)
    data class Date(val year: Int, val month: Int, val day: Int)
}

data class ReminderViewState(
    val title: String,
    val body: String,
    val time: ViewString,
    val date: ViewString,
    val recurrence: Int
)