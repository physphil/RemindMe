package com.physphil.android.remindme.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText

/**
 * View-related extension methods
 *
 * Copyright (c) 2018 Phil Shadlyn
 */

/**
 * Set visibility of a view to either VISIBLE (true) or GONE (false).
 * @param visible if the view should be visible or not
 */
fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

/**
 * Sets the text on the [EditText], then moves the cursor to the end of the field.
 */
fun EditText.setTextMoveCursorToEnd(text: CharSequence) {
    setText(text)
    setSelection(text.length)
}