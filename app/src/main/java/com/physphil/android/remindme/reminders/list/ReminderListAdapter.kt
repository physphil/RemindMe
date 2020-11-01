package com.physphil.android.remindme.reminders.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.util.ViewString
import com.physphil.android.remindme.util.displayDate
import com.physphil.android.remindme.util.displayTime
import com.physphil.android.remindme.util.setVisibility
import kotlinx.android.synthetic.main.view_row_date_header.view.reminderItemDateHeaderView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemBodyView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemRecurrenceView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemTimeView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemTitleView

private const val VIEW_TYPE_DATE = 0
private const val VIEW_TYPE_REMINDER = 1

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListAdapter : RecyclerView.Adapter<ReminderListAdapter.ReminderListViewHolder>() {

    interface ReminderListAdapterClickListener {
        fun onReminderClicked(reminder: Reminder)
    }

    sealed class ReminderListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class Date(view: View) : ReminderListViewHolder(view)
        class Reminder(view: View) : ReminderListViewHolder(view)
    }

    private var listener: ReminderListAdapterClickListener? = null
    private val items = mutableListOf<ListItem>()

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ReminderListViewHolder, position: Int) {
        when (holder) {
            is ReminderListViewHolder.Date -> holder.bind((items[position] as ListItem.Header).date)
            is ReminderListViewHolder.Reminder -> holder.bind((items[position] as ListItem.Entry).reminder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_DATE) {
            val view = inflater.inflate(R.layout.view_row_date_header, parent, false)
            ReminderListViewHolder.Date(view)
        } else {
            val view = inflater.inflate(R.layout.view_row_reminder_list, parent, false)
            ReminderListViewHolder.Reminder(view)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is ListItem.Header -> VIEW_TYPE_DATE
            is ListItem.Entry -> VIEW_TYPE_REMINDER
        }

    fun setOnClickListener(listener: ReminderListAdapterClickListener) {
        this.listener = listener
    }

    fun setReminderList(reminders: List<Reminder>) {
        this.items.clear()
        this.items.addAll(reminders.groupByHeader())
        notifyDataSetChanged()
    }

    operator fun get(position: Int): Reminder? =
        when (val item = items[position]) {
            is ListItem.Entry -> item.reminder
            else -> null
        }

    private fun ReminderListViewHolder.Date.bind(date: ViewString) {
        itemView.reminderItemDateHeaderView.text = date.getText(itemView.context)
    }

    private fun ReminderListViewHolder.Reminder.bind(reminder: Reminder) {
        with(itemView) {
            setOnClickListener {
                listener?.onReminderClicked(reminder)
            }

            reminderItemTimeView.text = reminder.displayTime.getText(context)
            reminderItemTitleView.text = reminder.title

            // Hide description if not entered
            if (reminder.body.isNotEmpty()) {
                reminderItemBodyView.setVisibility(true)
                reminderItemBodyView.text = reminder.body
            } else {
                reminderItemBodyView.setVisibility(false)
            }

            // Hide recurrence if it is a single alarm
            if (reminder.recurrence != com.physphil.android.remindme.models.Recurrence.NONE) {
                reminderItemRecurrenceView.setVisibility(true)
                reminderItemRecurrenceView.setText(reminder.recurrence.displayString)
            } else {
                reminderItemRecurrenceView.setVisibility(false)
            }
        }
    }

    private fun List<Reminder>.groupByHeader(): List<ListItem> =
        this.groupBy { it.displayDate }
            .flatMap { (key, value) ->
                listOf(ListItem.Header(key)) + value.map { ListItem.Entry(it) }
            }

    private sealed class ListItem {
        data class Header(val date: ViewString) : ListItem()
        data class Entry(val reminder: Reminder) : ListItem()
    }
}