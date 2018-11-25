package com.physphil.android.remindme.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.physphil.android.remindme.R
import kotlinx.android.synthetic.main.view_reminder_entry_button.view.*

class ReminderEntryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var listener: OnClickListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_reminder_entry_button, this)

        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ReminderEntryButton)
            val text = ta.getString(R.styleable.ReminderEntryButton_button_text)
            val icon = ta.getResourceId(R.styleable.ReminderEntryButton_button_icon, 0)

            if (icon > 0) {
                setIcon(icon)
            }

            text?.let { setText(it) }

            ta.recycle()
        }

        view.reminderEntryButtonView.setOnClickListener {
            listener?.onClick(it)
        }

        view.reminderEntryButtonIconView.setOnClickListener {
            listener?.onClick(it)
        }
    }

    fun setText(text: String) {
        reminderEntryButtonView.text = text
    }

    fun setText(res: Int) {
        setText(context.getString(res))
    }

    fun setIcon(@DrawableRes icon: Int) {
        reminderEntryButtonIconView.setImageResource(icon)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        listener = l
    }
}