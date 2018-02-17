package com.physphil.android.remindme.reminders.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.room.entities.Reminder
import com.physphil.android.remindme.util.setVisibility

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
    private val headerReminder: Reminder by lazy {
        val header = Reminder()
        header.id = HEADER_ID
        header
    }

    /**
     * The text that will be displayed in the RecyclerView's header. Header will be invisible if not specified
     */
    var headerText = ""

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder is HeaderViewHolder) {
            holder.header.setVisibility(headerText.isNotEmpty())
            holder.header.text = headerText
        }
        else if (holder is ReminderViewHolder) {
            val reminder = reminders[position]
            holder.date.text = reminder.getDisplayDate(holder.date.context)
            holder.time.text = reminder.getDisplayTime(holder.time.context)
            holder.title.text = reminder.title

            // Hide description if not entered
            if (reminder.body.isNotEmpty()) {
                holder.body.setVisibility(true)
                holder.body.text = reminder.body
            }
            else {
                holder.body.setVisibility(false)
            }

            // Hide recurrence if it is a single alarm
            if (reminder.recurrence != Recurrence.NONE) {
                holder.recurrence.setVisibility(true)
                holder.recurrence.setText(reminder.recurrence.displayString)
            }
            else {
                holder.recurrence.setVisibility(false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.header_reminder_list, parent, false)
            HeaderViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_reminder_list, parent, false)
            ReminderViewHolder(view)
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
    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class HeaderViewHolder(view: View) : ViewHolder(view) {

        @BindView(R.id.reminder_list_header)
        lateinit var header: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }

    inner class ReminderViewHolder(view: View) : ViewHolder(view), View.OnClickListener {

        @BindView(R.id.reminder_list_delete)
        lateinit var delete: Button

        @BindView(R.id.reminder_list_date)
        lateinit var date: TextView

        @BindView(R.id.reminder_list_time)
        lateinit var time: TextView

        @BindView(R.id.reminder_list_recurrence)
        lateinit var recurrence: TextView

        @BindView(R.id.reminder_list_title)
        lateinit var title: TextView

        @BindView(R.id.reminder_list_body)
        lateinit var body: TextView

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener(this)
            delete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.reminder_list_delete -> listener?.onDeleteReminder(reminders[adapterPosition])
                else -> listener?.onReminderClicked(reminders[adapterPosition])
            }
        }
    }
}