package com.physphil.android.remindme.reminders

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Recurrence

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class RecurrencePickerDialog : DialogFragment() {

    interface OnRecurrenceSetListener {
        fun onRecurrenceSet(recurrence: Recurrence)
    }

    private var listener: OnRecurrenceSetListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRecurrenceSetListener) {
            listener = context
        }
        else {
            throw ClassCastException("Hosting Activity must implement OnRecurrenceSetListener interface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Determine the index of the string corresponding to the currently selected recurrence
        val recurrences = resources.getStringArray(R.array.recurrence_options)
        val selectedRecurrenceId = arguments?.getInt(ARGS_RECURRENCE, Recurrence.NONE.id) ?: Recurrence.NONE.id
        val selectedRecurrence = Recurrence.fromId(selectedRecurrenceId)
        val selectedRecurrenceIndex = recurrences.indexOfFirst { it == getString(selectedRecurrence.getDisplayString()) }

        return AlertDialog.Builder(activity!!)
                .setTitle(R.string.title_select_recurrence)
                .setSingleChoiceItems(R.array.recurrence_options, selectedRecurrenceIndex, { dialog, which ->
                    // Determine which Recurrence was selected from array index
                    val recurrence = Recurrence.values().first { getString(it.getDisplayString()) == recurrences[which] }
                    listener?.onRecurrenceSet(recurrence)
                    dismiss()
                })
                .show()
    }

    companion object {
        const val TAG = "com.physphil.android.remindme.TAG_RECURRENCE_PICKER_DIALOG"
        private const val ARGS_RECURRENCE = "recurrence"

        fun newInstance(recurrence: Recurrence): RecurrencePickerDialog {
            val dialog = RecurrencePickerDialog()
            val args = Bundle()
            args.putInt(ARGS_RECURRENCE, recurrence.id)
            dialog.arguments = args
            return dialog
        }
    }
}