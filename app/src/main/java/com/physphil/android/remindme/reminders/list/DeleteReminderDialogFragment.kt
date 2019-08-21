package com.physphil.android.remindme.reminders.list

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Reminder

/**
 * Confirmation dialog before deleting a Reminder
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class DeleteReminderDialogFragment : DialogFragment() {

    interface Listener {
        fun onConfirmDeleteReminder(reminder: Reminder)
        fun onCancel()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw ClassCastException("Calling Activity must implement Listener interface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val reminder = arguments!!.getParcelable<Reminder>(ARG_REMINDER)
        return AlertDialog.Builder(activity!!)
            .setTitle(R.string.dialog_title_delete_reminder)
            .setMessage(R.string.dialog_message_delete_reminder)
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                listener?.onConfirmDeleteReminder(reminder)
            }
            .setNegativeButton(R.string.btn_no) { _, _ ->
                listener?.onCancel()
            }
            .show()
    }

    companion object {
        const val TAG = "com.physphil.android.remindme.DeleteReminderDialogFragment"
        private const val ARG_REMINDER = "arg_reminder"

        fun newInstance(reminder: Reminder) = DeleteReminderDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_REMINDER, reminder)
            }
        }
    }
}