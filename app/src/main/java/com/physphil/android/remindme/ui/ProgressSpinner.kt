package com.physphil.android.remindme.ui

import android.content.Context
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.physphil.android.remindme.R

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ProgressSpinner : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val view = inflate(context, R.layout.view_progress_spinner, this)
        ButterKnife.bind(this, view)

        attrs?.let {
            // Load any xml-specified attributes
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressSpinner)
            val res = ta.getResourceId(R.styleable.ProgressSpinner_spinner_message, 0)
            val str = ta.getString(R.styleable.ProgressSpinner_spinner_message)

            if (res != 0) {
                setMessage(res)
            }
            else if (str != null) {
                setMessage(str)
            }
            ta.recycle()
        }
    }

    @BindView(R.id.progress_spinner)
    lateinit var spinner: ProgressBar

    @BindView(R.id.progress_message)
    lateinit var message: TextView

    fun setMessage(@StringRes message: Int) {
        this.message.setText(message)
    }

    fun setMessage(message: String) {
        this.message.text = message
    }
}