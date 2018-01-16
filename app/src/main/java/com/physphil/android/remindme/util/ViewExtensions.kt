package com.physphil.android.remindme.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

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