package com.physphil.android.remindme.util

import java.util.Calendar

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

fun Calendar.endOfDay(): Calendar = this.apply {
    set(Calendar.HOUR_OF_DAY, 17)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.tonight(): Calendar = this.apply {
    set(Calendar.HOUR_OF_DAY, 19)
    set(Calendar.MINUTE, 30)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.tomorrowMorning(): Calendar = this.apply {
    val day = get(Calendar.DAY_OF_YEAR)
    set(Calendar.DAY_OF_YEAR, day + 1)
    set(Calendar.HOUR_OF_DAY, 7)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}