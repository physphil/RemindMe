package com.physphil.android.remindme.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.physphil.android.remindme.R
import com.physphil.android.remindme.util.setTextMoveCursorToEnd
import kotlinx.android.synthetic.main.view_reminder_entry.view.reminderEntryTextContainerView
import kotlinx.android.synthetic.main.view_reminder_entry.view.reminderEntryTextView

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderEntryField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textChangedListener: ((String) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_reminder_entry, this)

        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ReminderEntryField)
            val title = ta.getResourceId(R.styleable.ReminderEntryField_field_title, 0)
            val icon = ta.getResourceId(R.styleable.ReminderEntryField_field_icon, 0)

            if (title > 0) {
                setTitle(title)
            }

            if (icon > 0) {
                setIcon(icon)
            }

            ta.recycle()
        }

        view.reminderEntryTextView.addTextChangedListener { text ->
            textChangedListener?.invoke(text.toString())
        }
    }

    fun setTitle(@StringRes title: Int) {
        reminderEntryTextContainerView.hint = context.getString(title)
    }

    fun setText(text: String, moveCursorToEnd: Boolean = false) {
        when (moveCursorToEnd) {
            true -> reminderEntryTextView.setTextMoveCursorToEnd(text)
            false -> reminderEntryTextView.setText(text)
        }
    }

    fun setIcon(@DrawableRes icon: Int) {
        val drawable = resources.getDrawable(icon, null)
        reminderEntryTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    fun setOnTextChangedListener(listener: (String) -> Unit) {
        textChangedListener = listener
    }
}