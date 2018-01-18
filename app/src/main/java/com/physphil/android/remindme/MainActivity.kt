package com.physphil.android.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.reminders.list.ReminderListAdapter
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.entities.Reminder
import com.physphil.android.remindme.ui.ProgressSpinner
import com.physphil.android.remindme.util.setVisibility

class MainActivity : BaseActivity(), ReminderListAdapter.ReminderListAdapterClickListener {

    @BindView(R.id.reminder_list_recyclerview)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.reminder_list_spinner)
    lateinit var spinner: ProgressSpinner

    @BindView(R.id.reminder_list_empty)
    lateinit var empty: TextView

    private val adapter = ReminderListAdapter()
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this, MainActivityViewModelFactory(ReminderRepo(AppDatabase.getInstance(this).reminderDao())))
                .get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        adapter.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Create required notification channel on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_NOTIFICATIONS, getString(R.string.channel_notifications), NotificationManager.IMPORTANCE_HIGH)

            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        viewModel.getSpinnerVisibility().observe(this, spinnerVisibilityObserver)
        viewModel.getListVisibility().observe(this, listVisibilityObserver)
        viewModel.getEmptyVisibility().observe(this, emptyVisibilityObserver)
        viewModel.getReminderList().observe(this, reminderListObserver)
    }

    private val spinnerVisibilityObserver = Observer<Boolean> {
        it?.let {
            spinner.setVisibility(it)
        }
    }

    private val listVisibilityObserver = Observer<Boolean> {
        it?.let { recyclerView.setVisibility(it) }
    }

    private val emptyVisibilityObserver = Observer<Boolean> {
        it?.let { empty.setVisibility(it) }
    }

    private val reminderListObserver = Observer<List<Reminder>> {
        it?.let {
            adapter.setReminderList(it)
            viewModel.reminderListUpdated()
        }
    }

    @OnClick(R.id.reminder_list_fab)
    fun onAddReminderClick() {
        startActivity(ReminderActivity.intent(this))
    }

    override fun onReminderClicked(reminder: Reminder) {
        startActivity(ReminderActivity.intent(this, reminder.id))
    }
}
