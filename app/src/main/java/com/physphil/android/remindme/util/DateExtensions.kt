package com.physphil.android.remindme.util

import java.util.*

/**
 * Copyright (c) 2018 Phil Shadlyn
 */

fun Calendar.isToday(): Boolean {
    val today = Calendar.getInstance()
    return (get(Calendar.ERA) == today.get(Calendar.ERA)
            && get(Calendar.YEAR) == today.get(Calendar.YEAR)
            && get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
}

fun Calendar.isTomorrow(): Boolean {
    val today = Calendar.getInstance()
    return (get(Calendar.ERA) == today.get(Calendar.ERA)
            && get(Calendar.YEAR) == today.get(Calendar.YEAR)
            && get(Calendar.DAY_OF_YEAR) == (today.get(Calendar.DAY_OF_YEAR) + 1))
}

fun Calendar.isNow(): Boolean {
    val now = System.currentTimeMillis()
    return Math.abs(timeInMillis - now) < (1000 * 5)   // Considered "now" if within 5 seconds of current time
}