package com.physphil.android.remindme.reminders

import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.BaseActivity
import com.physphil.android.remindme.R
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.models.PresetTime
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.reminders.list.DeleteReminderDialogFragment
import com.physphil.android.remindme.util.ViewString
import kotlinx.android.synthetic.main.activity_reminder.*

/**
 * Create a new [Reminder] or edit an existing one.
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderActivity : BaseActivity(),
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener,
    RecurrencePickerDialog.OnRecurrenceSetListener,
    DeleteReminderDialogFragment.Listener {

    private lateinit var viewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setHomeArrowBackNavigation()
        bindViews()

        // Setup ViewModel
        val id = intent.getStringExtra(EXTRA_REMINDER_ID)
        val presetTime = PresetTime.fromId(
            intent.getIntExtra(EXTRA_REMINDER_PRESET_TIME, PresetTime.ID_UNKNOWN)
        )
        val factory = ReminderViewModel.Factory(
            repo = Injector.provideReminderRepo(this),
            id = id,
            presetTime = presetTime
        )
        viewModel = ViewModelProvider(this, factory).get(ReminderViewModel::class.java)
        viewModel.bind(this)
    }

    private fun bindViews() {
        reminderTitleView.setOnTextChangedListener {
            viewModel.updateTitle(it)
        }

        reminderBodyView.setOnTextChangedListener {
            viewModel.updateBody(it)
        }

        reminderTimeView.setOnClickListener {
            viewModel.openTimePicker()
        }

        reminderDateView.setOnClickListener {
            viewModel.openDatePicker()
        }

        reminderRecurrenceView.setOnClickListener {
            viewModel.openRecurrencePicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val delete = menu.findItem(R.id.menu_delete)
        delete?.let { viewModel.prepareOptionsMenuItems(delete) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                viewModel.saveReminder()
                finish()
                true
            }
            R.id.menu_delete -> {
                viewModel.confirmDeleteReminder()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // region OnTimeSetListener implementation
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.updateTime(hourOfDay, minute)
    }
    // endregion

    // region OnDateSetListener implementation
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.updateDate(year, month, dayOfMonth)
    }
    // endregion

    // region OnRecurrenceSetListener implementation
    override fun onRecurrenceSet(recurrence: Recurrence) {
        viewModel.updateRecurrence(recurrence)
    }
    // endregion

    // region DeleteReminderDialogFragment.Listener implementation
    override fun onConfirmDeleteReminder(reminder: Reminder) {
        viewModel.deleteReminder()
    }

    override fun onCancel() {
        // do nothing
    }
    // endregion

    private fun ReminderViewModel.bind(lifecycleOwner: LifecycleOwner) {
        reminderLiveData.observe(lifecycleOwner, Observer { state ->
            reminderTitleView.setText(state.title, true)
            reminderBodyView.setText(state.body)
            when (state.time) {
                is ViewString.Integer -> reminderTimeView.setText(state.time.resId)
                is ViewString.String -> reminderTimeView.setText(state.time.value)
            }
            when (state.date) {
                is ViewString.Integer -> reminderDateView.setText(state.date.resId)
                is ViewString.String -> reminderDateView.setText(state.date.value)
            }
            reminderRecurrenceView.setText(state.recurrence)
        })

        reminderTimeLiveData.observe(lifecycleOwner, Observer { time ->
            when (time) {
                is ViewString.Integer -> reminderTimeView.setText(time.resId)
                is ViewString.String -> reminderTimeView.setText(time.value)
            }
        })

        reminderDateLiveData.observe(lifecycleOwner, Observer { date ->
            when (date) {
                is ViewString.Integer -> reminderDateView.setText(date.resId)
                is ViewString.String -> reminderDateView.setText(date.value)
            }
        })

        reminderRecurrenceLiveData.observe(lifecycleOwner, Observer { recurrence ->
            reminderRecurrenceView.setText(recurrence)
        })

        toolbarTitleLiveData.observe(lifecycleOwner, Observer { title ->
            setToolbarTitle(title)
        })

        clearNotificationEvent.observe(lifecycleOwner, Observer { id ->
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                cancel(id)
            }
        })

        confirmDeleteEvent.observe(lifecycleOwner, Observer { reminder ->
            DeleteReminderDialogFragment.newInstance(reminder)
                .show(supportFragmentManager, DeleteReminderDialogFragment.TAG)
        })

        closeActivityEvent.observe(lifecycleOwner, Observer {
            finish()
        })

        openTimePickerEvent.observe(lifecycleOwner, Observer { time ->
            TimePickerDialog(
                this@ReminderActivity,
                R.style.Pickers,
                this@ReminderActivity,
                time.hour,
                time.minute,
                false
            ).show()
        })

        openDatePickerEvent.observe(lifecycleOwner, Observer { date ->
            DatePickerDialog(
                this@ReminderActivity,
                R.style.Pickers,
                this@ReminderActivity,
                date.year,
                date.month,
                date.day
            ).show()
        })

        openRecurrencePickerEvent.observe(lifecycleOwner, Observer { recurrence ->
            RecurrencePickerDialog.newInstance(recurrence)
                .show(supportFragmentManager, RecurrencePickerDialog.TAG)
        })
    }

    companion object {
        private const val EXTRA_REMINDER_ID = "com.physphil.android.remindme.EXTRA_REMINDER_ID"
        private const val EXTRA_REMINDER_PRESET_TIME = "com.physphil.android.remindme.EXTRA_REMINDER_PRESET_TIME"

        fun intent(context: Context, reminderId: String? = null): Intent =
            Intent(context, ReminderActivity::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
            }
    }
}