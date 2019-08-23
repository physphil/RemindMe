package com.physphil.android.remindme.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.physphil.android.remindme.R
import com.physphil.android.remindme.util.setTextMoveCursorToEnd
import kotlinx.android.synthetic.main.view_reminder_entry.view.*

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderEntryField @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textChangedListener: ((String) -> Unit)? = null
    private val editText: EditText

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_reminder_entry, this)
        val id = View.generateViewId()
        view.reminderEntryContentView.id = id
        editText = view.findViewById(id)

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

//        view.reminderEntryContentView.addTextChangedListener(object : TextWatcher {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                Log.d("phil", "afterTextChanged, text = $text")
                textChangedListener?.invoke(text.toString())
            }

            override fun beforeTextChanged(text: CharSequence?, s: Int, c: Int, a: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun setTitle(@StringRes title: Int) {
        reminderEntryTitleView.setText(title)
    }

    fun setText(text: String, moveCursorToEnd: Boolean = false) {
        when(moveCursorToEnd) {
            true -> editText.setTextMoveCursorToEnd(text)
            false -> editText.setText(text)
        }
    }

    fun setIcon(@DrawableRes icon: Int) {
        reminderEntryIconView.setImageResource(icon)
    }

    fun setOnTextChangedListener(listener: (String) -> Unit) {
        textChangedListener = listener
    }
}