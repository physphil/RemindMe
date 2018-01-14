package com.physphil.android.remindme.reminders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.physphil.android.remindme.BaseActivity
import com.physphil.android.remindme.R
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.entities.Reminder
import java.util.*

/**
 * Create a new reminder or edit and existing one.
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    @BindView(R.id.reminder_title_text)
    lateinit var titleText: EditText

    @BindView(R.id.reminder_body_text)
    lateinit var bodyText: EditText

    @BindView(R.id.reminder_time_text)
    lateinit var timeText: EditText

    @BindView(R.id.reminder_date_text)
    lateinit var dateText: EditText

    @BindView(R.id.reminder_repeat_text)
    lateinit var repeatText: EditText

    private val viewModel: ReminderViewModel by lazy {
        val id = intent.getStringExtra(EXTRA_REMINDER_ID)
        ViewModelProviders.of(this, ReminderViewModelFactory(id, ReminderRepo(AppDatabase.getInstance(this).reminderDao()), JobRequestScheduler))
                .get(ReminderViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setHomeArrowBackNavigation()
        ButterKnife.bind(this)

        // Will either be called immediately with stored value, or will be updated upon successful read from database
        viewModel.getReminder().observe(this, reminderObserver)
        viewModel.getReminderTime().observe(this, timeObserver)
        viewModel.getReminderDate().observe(this, dateObserver)
        viewModel.getReminderRecurrence().observe(this, recurrenceObserver)
        setToolbarTitle(viewModel.toolbarTitle)
    }

    private val reminderObserver = Observer<Reminder> {
        it?.let {
            titleText.setText(it.title, TextView.BufferType.EDITABLE)
            bodyText.setText(it.body, TextView.BufferType.EDITABLE)
            timeText.setText(it.getDisplayTime(), TextView.BufferType.EDITABLE)
            dateText.setText(it.getDisplayDate(), TextView.BufferType.EDITABLE)
            repeatText.setText(it.recurrence.getDisplayString(), TextView.BufferType.EDITABLE)
        }
    }

    private val timeObserver = Observer<String> {
        it?.let { timeText.setText(it, TextView.BufferType.EDITABLE)}
    }

    private val dateObserver = Observer<String> {
        it?.let { dateText.setText(it, TextView.BufferType.EDITABLE)}
    }

    private val recurrenceObserver = Observer<Int> {
        it?.let { repeatText.setText(it, TextView.BufferType.EDITABLE)}
    }

    @OnTextChanged(R.id.reminder_title_text)
    fun onTitleChanged(text: CharSequence) {
        viewModel.updateTitle(text.toString())
    }

    @OnTextChanged(R.id.reminder_body_text)
    fun onBodyChanged(text: CharSequence) {
        viewModel.updateBody(text.toString())
    }

    @OnClick(R.id.reminder_time_text, R.id.reminder_time_icon, R.id.reminder_time_title)
    fun onTimeClicked() {
        val calendar = viewModel.getReminderValue().time
        TimePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false).show()
    }

    @OnClick(R.id.reminder_date_text, R.id.reminder_date_icon, R.id.reminder_date_title)
    fun onDateClicked() {
        val calendar = viewModel.getReminderValue().time
        DatePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    @OnClick(R.id.reminder_repeat_text, R.id.reminder_repeat_icon, R.id.reminder_repeat_title)
    fun onRecurrenceClicked() {
        RecurrencePickerDialog.newInstance(viewModel.getReminderValue().recurrence)
                .show(supportFragmentManager, RecurrencePickerDialog.TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                viewModel.saveReminder()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.updateTime(hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.updateDate(year, month, dayOfMonth)
    }

    override fun onRecurrenceSet(recurrence: Recurrence) {
        viewModel.updateRecurrence(recurrence)
    }

    companion object {
        private const val EXTRA_REMINDER_ID = "com.physphil.android.remindme.EXTRA_REMINDER_ID"

        fun intent(context: Context, reminderId: String? = null): Intent {
            val intent = Intent(context, ReminderActivity::class.java)
            intent.putExtra(EXTRA_REMINDER_ID, reminderId)
            return intent
        }
    }
}