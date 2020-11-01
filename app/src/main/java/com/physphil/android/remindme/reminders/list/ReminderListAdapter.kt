package com.physphil.android.remindme.reminders.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.util.ViewString
import com.physphil.android.remindme.util.displayDate
import com.physphil.android.remindme.util.displayTime
import com.physphil.android.remindme.util.setVisibility
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemBodyView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemDateView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemRecurrenceView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemTimeView
import kotlinx.android.synthetic.main.view_row_reminder_list.view.reminderItemTitleView

private const val VIEW_TYPE_REMINDER = 1

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListAdapter : RecyclerView.Adapter<ReminderListAdapter.ViewHolder>() {

    interface ReminderListAdapterClickListener {
        fun onReminderClicked(reminder: Reminder)
    }

    private var listener: ReminderListAdapterClickListener? = null
    private val reminders = mutableListOf<Reminder>()

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        with(holder.itemView) {
            setOnClickListener {
                listener?.onReminderClicked(reminder)
            }

            reminderItemDateView.text = when (val date = reminder.displayDate) {
                is ViewString.String -> date.value
                is ViewString.Integer -> context.getString(date.resId)
            }
            reminderItemTimeView.text = when (val time = reminder.displayTime) {
                is ViewString.String -> time.value
                is ViewString.Integer -> context.getString(time.resId)
            }
            reminderItemTitleView.text = reminder.title

            // Hide description if not entered
            if (reminder.body.isNotEmpty()) {
                reminderItemBodyView.setVisibility(true)
                reminderItemBodyView.text = reminder.body
            } else {
                reminderItemBodyView.setVisibility(false)
            }

            // Hide recurrence if it is a single alarm
            if (reminder.recurrence != Recurrence.NONE) {
                reminderItemRecurrenceView.setVisibility(true)
                reminderItemRecurrenceView.setText(reminder.recurrence.displayString)
            } else {
                reminderItemRecurrenceView.setVisibility(false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_row_reminder_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = VIEW_TYPE_REMINDER

    fun setOnClickListener(listener: ReminderListAdapterClickListener) {
        this.listener = listener
    }

    fun setReminderList(reminders: List<Reminder>) {
        DiffUtil.calculateDiff(
            ReminderDiffUtilCallback(
                oldList = this.reminders,
                newList = reminders
            )
        ).dispatchUpdatesTo(this)

        this.reminders.clear()
        this.reminders.addAll(reminders)
    }

    operator fun get(position: Int): Reminder = reminders[position]

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class ReminderDiffUtilCallback(
        private val oldList: List<Reminder>,
        private val newList: List<Reminder>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}