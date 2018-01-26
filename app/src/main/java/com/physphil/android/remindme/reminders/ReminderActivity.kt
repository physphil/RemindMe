package com.physphil.android.remindme.reminders

import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.physphil.android.remindme.BaseActivity
import com.physphil.android.remindme.R
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.reminders.list.DeleteReminderDialogFragment
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.entities.Reminder
import java.util.*

/**
 * Create a new reminder or edit and existing one.
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, RecurrencePickerDialog.OnRecurrenceSetListener,
        DeleteReminderDialogFragment.Listener {

    @BindView(R.id.reminder_title_text)
    lateinit var titleText: EditText

    @BindView(R.id.reminder_body_text)
    lateinit var bodyText: EditText

    @BindView(R.id.reminder_time_btn)
    lateinit var timeText: Button

    @BindView(R.id.reminder_date_btn)
    lateinit var dateText: Button

    @BindView(R.id.reminder_repeat_btn)
    lateinit var repeatText: Button

    private val viewModel: ReminderViewModel by lazy {
        val id = intent.getStringExtra(EXTRA_REMINDER_ID)
        ViewModelProviders.of(this, ReminderViewModelFactory(application as RemindMeApplication, id, ReminderRepo(AppDatabase.getInstance(this).reminderDao())))
                .get(ReminderViewModel::class.java)
    }
    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

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
        viewModel.getToolbarTitle().observe(this, toolbarTitleObserver)
        viewModel.getClearNotificationEvent().observe(this, clearNotificationEventObserver)
        viewModel.getConfirmDeleteEvent().observe(this, confirmDeleteObserver)
        viewModel.getCloseActivityEvent().observe(this, closeActivityObserver)
    }

    private val reminderObserver = Observer<Reminder> {
        it?.let {
            titleText.setText(it.title, TextView.BufferType.EDITABLE)
            titleText.setSelection(it.title.length)
            bodyText.setText(it.body, TextView.BufferType.EDITABLE)
            timeText.text = it.getDisplayTime(this)
            dateText.text = it.getDisplayDate(this)
            repeatText.setText(it.recurrence.getDisplayString())

            // Clear any notifications for this Reminder
            notificationManager.cancel(it.notificationId)
        }
    }

    private val timeObserver = Observer<String> {
        it?.let { timeText.text = it }
    }

    private val dateObserver = Observer<String> {
        it?.let { dateText.text = it }
    }

    private val recurrenceObserver = Observer<Int> {
        it?.let { repeatText.setText(it) }
    }

    private val toolbarTitleObserver = Observer<Int> {
        it?.let { setToolbarTitle(it) }
    }

    private val clearNotificationEventObserver = Observer<Int> {
        it?.let { notificationManager.cancel(it) }
    }

    private val confirmDeleteObserver = Observer<Void> {
        DeleteReminderDialogFragment.newInstance().show(supportFragmentManager, DeleteReminderDialogFragment.TAG)
    }

    private val closeActivityObserver = Observer<Void> {
        finish()
    }

    @OnTextChanged(R.id.reminder_title_text)
    fun onTitleChanged(text: CharSequence) {
        viewModel.updateTitle(text.toString())
    }

    @OnTextChanged(R.id.reminder_body_text)
    fun onBodyChanged(text: CharSequence) {
        viewModel.updateBody(text.toString())
    }

    @OnClick(R.id.reminder_time_btn, R.id.reminder_time_icon)
    fun onTimeClicked() {
        val calendar = viewModel.getReminderValue().time
        TimePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false).show()
    }

    @OnClick(R.id.reminder_date_btn, R.id.reminder_date_icon)
    fun onDateClicked() {
        val calendar = viewModel.getReminderValue().time
        DatePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    @OnClick(R.id.reminder_repeat_btn, R.id.reminder_repeat_icon)
    fun onRecurrenceClicked() {
        RecurrencePickerDialog.newInstance(viewModel.getReminderValue().recurrence)
                .show(supportFragmentManager, RecurrencePickerDialog.TAG)
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

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.updateTime(this, hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.updateDate(this, year, month, dayOfMonth)
    }

    override fun onRecurrenceSet(recurrence: Recurrence) {
        viewModel.updateRecurrence(recurrence)
    }

    // region DeleteReminderDialogFragment.Listener implementation
    override fun onDeleteReminder() {
        viewModel.deleteReminder()
    }

    override fun onCancel() {
        // do nothing
    }
    // endregion

    companion object {
        private const val EXTRA_REMINDER_ID = "com.physphil.android.remindme.EXTRA_REMINDER_ID"

        fun intent(context: Context, reminderId: String? = null): Intent {
            val intent = Intent(context, ReminderActivity::class.java)
            intent.putExtra(EXTRA_REMINDER_ID, reminderId)
            return intent
        }
    }
}