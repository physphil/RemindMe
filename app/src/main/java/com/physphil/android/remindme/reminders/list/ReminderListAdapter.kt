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
import com.physphil.android.remindme.room.entities.Reminder

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListAdapter : RecyclerView.Adapter<ReminderListAdapter.ViewHolder>() {

    interface ReminderListAdapterClickListener {
        fun onReminderClicked(reminder: Reminder)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        @BindView(R.id.reminder_list_edit)
        lateinit var edit: Button

        @BindView(R.id.reminder_list_date)
        lateinit var date: TextView

        @BindView(R.id.reminder_list_time)
        lateinit var time: TextView

        @BindView(R.id.reminder_list_title)
        lateinit var title: TextView

        @BindView(R.id.reminder_list_body)
        lateinit var body: TextView

        init {
            ButterKnife.bind(this, view)
            edit.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener?.onReminderClicked(reminders[adapterPosition])
        }
    }

    private val reminders = mutableListOf<Reminder>()
    private var listener: ReminderListAdapterClickListener? = null

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.date.text = reminder.getDisplayDate()
        holder.time.text = reminder.getDisplayTime()
        holder.title.text = reminder.title
        holder.body.text = reminder.body
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_reminder_list, parent, false)
        return ViewHolder(view)
    }

    fun setOnClickListener(listener: ReminderListAdapterClickListener) {
        this.listener = listener
    }

    fun setReminderList(reminders: List<Reminder>) {
        this.reminders.clear()
        this.reminders.addAll(reminders)
        notifyDataSetChanged()
    }
}