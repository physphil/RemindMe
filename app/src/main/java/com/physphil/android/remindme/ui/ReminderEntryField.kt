package com.physphil.android.remindme.ui

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.physphil.android.remindme.R

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderEntryField @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.reminder_entry_title)
    lateinit var title: TextView

    @BindView(R.id.reminder_entry_field)
    lateinit var text: EditText

    @BindView(R.id.reminder_entry_icon)
    lateinit var icon: ImageView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_reminder_entry, this)
        ButterKnife.bind(this, view)

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
    }

    fun setTitle(@StringRes title: Int) {
        this.title.setText(title)
    }

    fun setIcon(@DrawableRes icon: Int) {
        this.icon.setImageResource(icon)
    }
}