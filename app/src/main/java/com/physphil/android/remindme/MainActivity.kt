package com.physphil.android.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.reminders.list.DeleteAllDialogFragment
import com.physphil.android.remindme.reminders.list.ReminderListAdapter
import com.physphil.android.remindme.stats.StatsActivity
import com.physphil.android.remindme.ui.ReminderListDivider
import com.physphil.android.remindme.ui.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    ReminderListAdapter.ReminderListAdapterClickListener,
    DeleteAllDialogFragment.Listener {

    private val adapter = ReminderListAdapter()
    private lateinit var viewModel: MainActivityViewModel
    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val swipeToDeleteCallback = SwipeToDeleteCallback { position ->
        adapter[position]?.let { reminder ->
            viewModel.deleteReminder(reminder)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        val factory = MainActivityViewModel.Factory(Injector.provideReminderRepo(this))
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
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

        // Setup swipe callback
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(reminderListRecyclerView)
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
            R.id.menu_stats -> {
                startActivity(Intent(this, StatsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // region ReminderListAdapterClickListener implementation
    override fun onReminderClicked(reminder: Reminder) {
        startActivity(ReminderActivity.intent(this, reminder.id))
    }
    // endregion

    // region DeleteAllDialogFragment.Listener implementation
    override fun onDeleteAllReminders() {
        viewModel.deleteAllReminders()
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

        showDeleteConfirmationEvent.observe(lifecycleOwner, Observer { reminder ->
            Snackbar.make(reminderListRecyclerViewContainer, R.string.snackbar_undo_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo_action) { viewModel.undoDeleteReminder(reminder) }
                .setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.material_white))
                .apply {
                    view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.theme_dark_red))
                }
                .show()
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
