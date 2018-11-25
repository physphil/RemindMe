package com.physphil.android.remindme.ui

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.physphil.android.remindme.R
import kotlinx.android.synthetic.main.view_progress_spinner.view.*

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ProgressSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_progress_spinner, this)

        attrs?.let {
            // Load any xml-specified attributes
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressSpinner)
            val res = ta.getResourceId(R.styleable.ProgressSpinner_spinner_message, 0)
            val str = ta.getString(R.styleable.ProgressSpinner_spinner_message)

            if (res != 0) {
                setMessage(res)
            } else if (str != null) {
                setMessage(str)
            }
            ta.recycle()
        }
    }

    fun setMessage(@StringRes message: Int) {
        progressMessageView.setText(message)
    }

    fun setMessage(message: String) {
        progressMessageView.text = message
    }
}