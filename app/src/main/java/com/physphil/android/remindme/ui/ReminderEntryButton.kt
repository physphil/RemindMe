package com.physphil.android.remindme.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.physphil.android.remindme.R

class ReminderEntryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.reminder_entry_button_icon)
    lateinit var icon: ImageView

    @BindView(R.id.reminder_entry_button)
    lateinit var button: Button

    private var listener: OnClickListener? = null

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

    override fun setOnClickListener(l: OnClickListener?) {
        listener = l
    }

    @OnClick(R.id.reminder_entry_button, R.id.reminder_entry_button_icon)
    protected fun onButtonClicked(view: View) {
        listener?.onClick(view)
    }
}