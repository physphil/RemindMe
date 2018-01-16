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