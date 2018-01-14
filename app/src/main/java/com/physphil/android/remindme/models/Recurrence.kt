package com.physphil.android.remindme.models

import com.physphil.android.remindme.R

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
enum class Recurrence(val id: Int) {
    NONE(0),
    HOURLY(1),
    DAILY(2),
    WEEKLY(3),
    MONTHLY(4),
    YEARLY(5);

    fun getDisplayString(): Int {
        return when (this) {
            HOURLY -> R.string.recurrence_hourly
            DAILY -> R.string.recurrence_daily
            WEEKLY -> R.string.recurrence_weekly
            MONTHLY -> R.string.recurrence_monthly
            YEARLY -> R.string.recurrence_yearly
            else -> R.string.recurrence_none
        }
    }

    companion object {
        fun fromId(id: Int) = values().first { it.ordinal == id }
    }
}