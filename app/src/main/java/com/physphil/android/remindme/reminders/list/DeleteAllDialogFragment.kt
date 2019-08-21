package com.physphil.android.remindme.reminders.list

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.physphil.android.remindme.R

/**
 * Confirmation Dialog before deleting all Reminders
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class DeleteAllDialogFragment : DialogFragment() {

    interface Listener {
        fun onDeleteAllReminders()
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
        return AlertDialog.Builder(activity!!)
                .setTitle(R.string.dialog_title_delete_all_reminders)
                .setMessage(R.string.dialog_message_delete_all_reminders)
                .setPositiveButton(R.string.btn_yes, { _, _ ->
                    listener?.onDeleteAllReminders()
                })
                .setNegativeButton(R.string.btn_no, null)
                .show()
    }

    companion object {
        const val TAG = "com.physphil.android.remindme.DeleteAllDialogFragment"
        fun newInstance() = DeleteAllDialogFragment()
    }
}