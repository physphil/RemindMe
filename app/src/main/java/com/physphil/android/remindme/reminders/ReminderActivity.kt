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
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.physphil.android.remindme.BaseActivity
import com.physphil.android.remindme.R
import com.physphil.android.remindme.RemindMeApplication
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.reminders.list.DeleteReminderDialogFragment
import com.physphil.android.remindme.ui.ReminderEntryField
import com.physphil.android.remindme.util.getDisplayDate
import com.physphil.android.remindme.util.getDisplayTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Calendar

/**
 * Create a new observableReminder or edit and existing one.
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
class ReminderActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, RecurrencePickerDialog.OnRecurrenceSetListener,
        DeleteReminderDialogFragment.Listener {

    @BindView(R.id.reminder_title_title)
    lateinit var title: ReminderEntryField

    @BindView(R.id.reminder_body)
    lateinit var body: ReminderEntryField

    @BindView(R.id.reminder_time_btn)
    lateinit var timeText: Button

    @BindView(R.id.reminder_date_btn)
    lateinit var dateText: Button

    @BindView(R.id.reminder_repeat_btn)
    lateinit var repeatText: Button

    /** A [CompositeDisposable] to contain all active subscriptions. Should be cleared when Activity is destroyed */
    private val disposables = CompositeDisposable()

    private val viewModel: ReminderViewModel by lazy {
        val id = intent.getStringExtra(EXTRA_REMINDER_ID)
        ViewModelProviders.of(this, ReminderViewModelFactory(application as RemindMeApplication, id))
                .get(ReminderViewModel::class.java)
    }
    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setHomeArrowBackNavigation()
        ButterKnife.bind(this)
        bindViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun bindViewModel() {
        // Will either be called immediately with stored value, or will be updated upon successful read from database
        disposables.add(viewModel.observableReminder
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // on success. Save Reminder in viewmodel and update UI
                    viewModel.reminder = it
                    title.text.setText(it.title, TextView.BufferType.EDITABLE)
                    title.text.setSelection(it.title.length)
                    body.text.setText(it.body, TextView.BufferType.EDITABLE)
                    timeText.text = it.getDisplayTime(this)
                    dateText.text = it.getDisplayDate(this)
                    repeatText.setText(it.recurrence.displayString)

                    // Clear any notifications for this Reminder
                    notificationManager.cancel(it.notificationId)
                }, {
                    // on error
                }))

        viewModel.getReminderTime().observe(this, Observer { it?.let { timeText.text = it } })
        viewModel.getReminderDate().observe(this, Observer { it?.let { dateText.text = it } })
        viewModel.getReminderRecurrence().observe(this, Observer { it?.let { repeatText.setText(it) } })
        viewModel.getToolbarTitle().observe(this, Observer { it?.let { setToolbarTitle(it) } })
        viewModel.clearNotificationEvent.observe(this, Observer { it?.let { notificationManager.cancel(it) } })
        viewModel.confirmDeleteEvent.observe(this, Observer { DeleteReminderDialogFragment.newInstance().show(supportFragmentManager, DeleteReminderDialogFragment.TAG) })
        viewModel.closeActivityEvent.observe(this, Observer { finish() })
    }

    //todo - Move all these listeners to class of resepctive views
//    @OnTextChanged(R.id.reminder_title_text)
//    fun onTitleChanged(text: CharSequence) {
//        viewModel.updateTitle(text.toString())
//    }

//    @OnTextChanged(R.id.reminder_body_text)
//    fun onBodyChanged(text: CharSequence) {
//        viewModel.updateBody(text.toString())
//    }

    @OnClick(R.id.reminder_time_btn, R.id.reminder_time_icon)
    fun onTimeClicked() {
        val calendar = viewModel.reminder.time
        TimePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false).show()
    }

    @OnClick(R.id.reminder_date_btn, R.id.reminder_date_icon)
    fun onDateClicked() {
        val calendar = viewModel.reminder.time
        DatePickerDialog(this, R.style.Pickers, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    @OnClick(R.id.reminder_repeat_btn, R.id.reminder_repeat_icon)
    fun onRecurrenceClicked() {
        RecurrencePickerDialog.newInstance(viewModel.reminder.recurrence)
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

    // region OnTimeSetListener implementation
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.updateTime(this, hourOfDay, minute)
    }
    // endregion

    // region OnDateSetListener implementation
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.updateDate(this, year, month, dayOfMonth)
    }
    // endregion

    // region OnRecurrenceSetListener implementation
    override fun onRecurrenceSet(recurrence: Recurrence) {
        viewModel.updateRecurrence(recurrence)
    }
    // endregion

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