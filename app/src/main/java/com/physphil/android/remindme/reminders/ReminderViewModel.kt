package com.physphil.android.remindme.reminders

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    private val repo: ReminderRepo,
    private val scheduler: JobRequestScheduler,
    id: String? = null,
    presetTime: PresetTime? = null
) : ViewModel() {

    private val isNewReminder = (id == null)

    // Internal variable to keep track of Reminder state. LiveData should be updated whenever this
    // value changes, so it can provide the proper value on a config change.
    private lateinit var reminder: Reminder

    val reminderLiveData: LiveData<ViewState>

    private val _clearNotificationEvent = SingleLiveEvent<Int>()
    val clearNotificationEvent: LiveData<Int> = _clearNotificationEvent

    private val _confirmDeleteEvent = SingleLiveEvent<Reminder>()
    val confirmDeleteEvent: LiveData<Reminder> = _confirmDeleteEvent

    private val _closeActivityEvent = SingleLiveEvent<Unit>()
    val closeActivityEvent: LiveData<Unit> = _closeActivityEvent

    private val _openTimePickerEvent = SingleLiveEvent<Time>()
    val openTimePickerEvent: LiveData<Time> = _openTimePickerEvent

    private val _openDatePickerEvent = SingleLiveEvent<Date>()
    val openDatePickerEvent: LiveData<Date> = _openDatePickerEvent

    private val _openRecurrencePickerEvent = SingleLiveEvent<Recurrence>()
    val openRecurrencePickerEvent: LiveData<Recurrence> = _openRecurrencePickerEvent

    private val _reminderTimeLiveData = MutableLiveData<ViewString>()
    val reminderTimeLiveData: LiveData<ViewString> = _reminderTimeLiveData

    private val _reminderDateLiveData = MutableLiveData<ViewString>()
    val reminderDateLiveData: LiveData<ViewString> = _reminderDateLiveData

    private val _reminderRecurrenceLiveData = MutableLiveData<Int>()
    val reminderRecurrenceLiveData: LiveData<Int> = _reminderRecurrenceLiveData

    private val _toolbarTitleLiveData = MutableLiveData<Int>().apply {
        postValue(if (isNewReminder) R.string.title_add_reminder else R.string.title_edit_reminder)
    }
    val toolbarTitleLiveData: LiveData<Int> = _toolbarTitleLiveData

    init {
        reminderLiveData = if (id == null) {
            MutableLiveData<ViewState>().apply {
                // Create new reminder and save
                val newReminder = Reminder(time = presetTime?.time ?: Calendar.getInstance())
                reminder = newReminder
                value = newReminder.toViewState()
            }
        } else {
            Transformations.map(repo.getReminder(id)) {
                // Pull existing reminder from DB, save locally, and clear its notification
                reminder = it
                _clearNotificationEvent.postValue(reminder.notificationId)
                it.toViewState()
            }
        }
    }

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
            _openTimePickerEvent.postValue(
                Time(
                    hour = get(Calendar.HOUR_OF_DAY),
                    minute = get(Calendar.MINUTE)
                )
            )
        }
    }

    fun openDatePicker() {
        with(reminder.time) {
            _openDatePickerEvent.postValue(
                Date(
                    year = get(Calendar.YEAR),
                    month = get(Calendar.MONTH),
                    day = get(Calendar.DAY_OF_MONTH)
                )
            )
        }
    }

    fun openRecurrencePicker() {
        _openRecurrencePickerEvent.postValue(reminder.recurrence)
    }

    fun saveReminder() {
        // Set all second fields to 0 before saving, so alarm happens at exactly the specified time
        reminder.time.set(Calendar.SECOND, 0)
        reminder.time.set(Calendar.MILLISECOND, 0)

        if (isNewReminder) {
            reminder = reminder.copy(externalId = scheduleNotification(reminder))
            repo.insertReminder(reminder)
        } else {
            scheduler.cancelJob(reminder.externalId)
            reminder = reminder.copy(externalId = scheduleNotification(reminder))
            repo.updateReminder(reminder)
        }
    }

    fun confirmDeleteReminder() {
        _confirmDeleteEvent.postValue(reminder)
    }

    fun deleteReminder() {
        _clearNotificationEvent.postValue(reminder.notificationId)
        scheduler.cancelJob(reminder.externalId)
        repo.deleteReminder(reminder)
        _closeActivityEvent.postValue(Unit)
    }

    fun prepareOptionsMenuItems(delete: MenuItem) {
        delete.isVisible = !isNewReminder
    }

    /**
     * Schedule a notification for the Reminder
     * @param reminder the reminder to schedule a notification for
     * @return the job id of the newly scheduled notification
     */
    private fun scheduleNotification(reminder: Reminder): Int =
        scheduler.scheduleShowNotificationJob(
            time = reminder.time.timeInMillis,
            id = reminder.id,
            title = reminder.title,
            text = reminder.body,
            recurrence = reminder.recurrence.id
        )

    private fun Reminder.toViewState(): ViewState = ViewState(
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

    data class ViewState(
        val title: String,
        val body: String,
        val time: ViewString,
        val date: ViewString,
        val recurrence: Int
    )

    class Factory(
        private val repo: ReminderRepo,
        private val scheduler: JobRequestScheduler,
        private val id: String?,
        private val presetTime: PresetTime?
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
                return ReminderViewModel(repo, scheduler, id, presetTime) as T
            }

            throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")

        }
    }
}