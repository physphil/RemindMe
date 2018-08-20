package com.physphil.android.remindme.ui

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.physphil.android.remindme.R

class ReminderEntryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.reminder_entry_button_icon)
    lateinit var icon: ImageView

    @BindView(R.id.reminder_entry_button)
    lateinit var button: Button

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_reminder_entry_button, this)
        ButterKnife.bind(this, view)

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
    }

    fun setText(text: String) {
        this.button.text = text
    }

    fun setText(res: Int) {
        setText(context.getString(res))
    }

    fun setIcon(@DrawableRes icon: Int) {
        this.icon.setImageResource(icon)
    }
}