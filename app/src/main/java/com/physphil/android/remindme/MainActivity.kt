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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.physphil.android.remindme.inject.Injector
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.reminders.ReminderActivity
import com.physphil.android.remindme.reminders.list.DeleteAllDialogFragment
import com.physphil.android.remindme.reminders.list.DeleteReminderDialogFragment
import com.physphil.android.remindme.reminders.list.ReminderListAdapter
import com.physphil.android.remindme.ui.ReminderListDivider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    ReminderListAdapter.ReminderListAdapterClickListener,
    DeleteAllDialogFragment.Listener,
    DeleteReminderDialogFragment.Listener {

    private val adapter = ReminderListAdapter()
    private lateinit var viewModel: MainActivityViewModel
    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

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
        val factory = MainActivityViewModel.Factory(
            repo = Injector.provideReminderRepo(this),
            scheduler = Injector.provideJobRequestScheduler()
        )
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
        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(reminderListRecyclerView)
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
    private val itemSwipeCallback = object : ItemTouchHelper.SimpleCallback(
        /* dragDirs */ 0,
        /* swipeDirs */ ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter[viewHolder.adapterPosition]?.let { reminder ->
                viewModel.deleteReminder(reminder)
            }
        }
    }

    override fun onReminderClicked(reminder: Reminder) {
        startActivity(ReminderActivity.intent(this, reminder.id))
    }

    override fun onDeleteReminder(reminder: Reminder) {
    }
    // endregion

    // region DeleteAllDialogFragment.Listener implementation
    override fun onDeleteAllReminders() {
        viewModel.deleteAllReminders()
    }
    // endregion

    // region DeleteReminderDialogFragment.Listener implementation
    override fun onConfirmDeleteReminder(reminder: Reminder) {
    }

    override fun onCancel() {
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
            Snackbar.make(reminderListRecyclerViewContainer, R.string.snackbar_undo_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo_action) { viewModel.undoDeleteReminder() }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        viewModel.clearDeletedReminder()
                    }
                })
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
