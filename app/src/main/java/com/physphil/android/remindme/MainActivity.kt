package com.physphil.android.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.reminders.list.DeleteAllDialogFragment
import com.physphil.android.remindme.reminders.list.DeleteReminderDialogFragment
import com.physphil.android.remindme.reminders.list.ReminderListAdapter
import com.physphil.android.remindme.ui.ReminderListDivider
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(),
    ReminderListAdapter.ReminderListAdapterClickListener,
    DeleteAllDialogFragment.Listener,
    DeleteReminderDialogFragment.Listener {

    @Inject
    lateinit var viewModelFactory: MainActivityViewModelFactory

    private val adapter = ReminderListAdapter()
    private lateinit var viewModel: MainActivityViewModel
    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as RemindMeApplication).applicationComponent.inject(this)
        setupRecyclerview()
        bindViews()

        // Create required notification channel on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_NOTIFICATIONS,
                getString(R.string.channel_notifications),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Setup ViewModel
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainActivityViewModel::class.java)
        viewModel.bind(this)
    }

    private fun setupRecyclerview() {
        adapter.setOnClickListener(this)
        reminderListRecyclerView.adapter = adapter
        reminderListRecyclerView.layoutManager = LinearLayoutManager(this)
        reminderListRecyclerView.itemAnimator = DefaultItemAnimator()

        // Setup list divider
        val inset = resources.getDimensionPixelSize(R.dimen.reminder_divider_margin)
        val divider = ReminderListDivider(InsetDrawable(getDrawable(R.drawable.divider), inset, 0, inset, 0))
        reminderListRecyclerView.addItemDecoration(divider)

        // Setup random list header
        val headers = resources.getStringArray(R.array.reminder_list_headers)
        val index = (Math.random() * headers.size).toInt()
        adapter.headerText = headers[index]
    }

    private fun bindViews() {
        reminderListFabView.setOnClickListener {
            startActivity(ReminderActivity.intent(this))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_all -> {
                viewModel.confirmDeleteAllReminders()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // region ReminderListAdapterClickListener implementation
    override fun onReminderClicked(reminder: Reminder) {
        startActivity(ReminderActivity.intent(this, reminder.id))
    }

    override fun onDeleteReminder(reminder: Reminder) {
        viewModel.confirmDeleteReminder(reminder)
    }
    // endregion

    // region DeleteAllDialogFragment.Listener implementation
    override fun onDeleteAllReminders() {
        viewModel.deleteAllReminders()
    }
    // endregion

    // region DeleteReminderDialogFragment.Listener implementation
    override fun onConfirmDeleteReminder(reminder: Reminder) {
        viewModel.deleteReminder()
    }

    override fun onCancel() {
        viewModel.cancelDeleteReminder()
    }
    // endregion

    private fun MainActivityViewModel.bind(lifecycleOwner: LifecycleOwner) {
        clearNotificationEvent.observe(lifecycleOwner, Observer { delete ->
            when (delete) {
                is MainActivityViewModel.Delete.All -> notificationManager.cancelAll()
                is MainActivityViewModel.Delete.Single -> notificationManager.cancel(delete.id)
            }
        })

        showDeleteAllConfirmationEvent.observe(lifecycleOwner, Observer {
            DeleteAllDialogFragment.newInstance()
                .show(supportFragmentManager, DeleteAllDialogFragment.TAG)
        })

        showDeleteConfirmationEvent.observe(lifecycleOwner, Observer {
            DeleteReminderDialogFragment.newInstance(it)
                .show(supportFragmentManager, DeleteReminderDialogFragment.TAG)
        })

        spinnerVisibilityLiveData.observe(lifecycleOwner, Observer { visible ->
            reminderListSpinnerView.isVisible = visible
        })

        emptyVisibilityLiveData.observe(lifecycleOwner, Observer { visible ->
            reminderListEmptyView.isVisible = visible
        })

        reminderList.observe(lifecycleOwner, Observer { reminders ->
            adapter.setReminderList(reminders)
            viewModel.reminderListUpdated(reminders.size)
            reminderListFabView.show()  // make sure the fab is always showing when the list is updated
        })
    }
}
