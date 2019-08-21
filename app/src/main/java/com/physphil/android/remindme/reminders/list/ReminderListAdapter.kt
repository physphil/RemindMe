package com.physphil.android.remindme.reminders.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.util.getDisplayDate
import com.physphil.android.remindme.util.getDisplayTime
import com.physphil.android.remindme.util.setVisibility
import kotlinx.android.synthetic.main.view_header_reminder_list.view.*
import kotlinx.android.synthetic.main.view_row_reminder_list.view.*

private const val HEADER_ID = "header_id"
private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListAdapter : RecyclerView.Adapter<ReminderListAdapter.ViewHolder>() {

    interface ReminderListAdapterClickListener {
        fun onReminderClicked(reminder: Reminder)
        fun onDeleteReminder(reminder: Reminder)
    }

    private var listener: ReminderListAdapterClickListener? = null
    private val reminders = mutableListOf<Reminder>()

    /** Reminder object representing a header to add to the list */
    private val headerReminder = Reminder(id = HEADER_ID)

    /**
     * The text that will be displayed in the RecyclerView's header. Header will be invisible if not specified
     */
    var headerText = ""

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder is ViewHolder.Header) {
            with(holder.itemView) {
                reminderItemHeaderView.setVisibility(headerText.isNotEmpty())
                reminderItemHeaderView.text = headerText
            }
        }
        else if (holder is ViewHolder.Reminder) {
            val reminder = reminders[position]
            with(holder.itemView) {
                setOnClickListener {
                    listener?.onReminderClicked(reminder)
                }

                reminderItemDeleteView.setOnClickListener {
                    listener?.onDeleteReminder(reminder)
                }

                reminderItemDateView.text = reminder.getDisplayDate(context)
                reminderItemTimeView.text = reminder.getDisplayTime(context)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_header_reminder_list, parent, false)
            ViewHolder.Header(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_row_reminder_list, parent, false)
            ViewHolder.Reminder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val reminder = reminders[position]
        return when (reminder.id) {
            HEADER_ID -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_ITEM
        }
    }

    fun setOnClickListener(listener: ReminderListAdapterClickListener) {
        this.listener = listener
    }

    fun setReminderList(reminders: List<Reminder>) {
        this.reminders.clear()
        if (reminders.isNotEmpty()) {
            this.reminders.add(headerReminder)
            this.reminders.addAll(reminders)
        }
        notifyDataSetChanged()
    }


    /*
     *  The ViewHolders used for both the Header and Reminder. They both extend from RecyclerView.ViewHolder
     *  in order to work with RecyclerView
     */
    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class Header(view: View) : ViewHolder(view)
        class Reminder(view: View) : ViewHolder(view)
    }
}