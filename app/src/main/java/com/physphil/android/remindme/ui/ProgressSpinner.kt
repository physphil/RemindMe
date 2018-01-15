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
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @BindView(R.id.progress_spinner)
    lateinit var spinner: ProgressBar

    @BindView(R.id.progress_message)
    lateinit var message: TextView

    init {
        val view = inflate(context, R.layout.progress_spinner, this)
        ButterKnife.bind(this, view)
    }

    fun setMessage(@StringRes message: Int) {
        this.message.setText(message)
    }
}