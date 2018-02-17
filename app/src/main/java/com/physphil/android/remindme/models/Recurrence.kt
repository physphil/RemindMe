package com.physphil.android.remindme.models

import com.physphil.android.remindme.R

/**
 * Recurrence options for a Reminder
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
enum class Recurrence(val id: Int, val displayString: Int) {
    NONE(0, R.string.recurrence_none),
    HOURLY(1, R.string.recurrence_hourly),
    DAILY(2, R.string.recurrence_daily),
    WEEKLY(3, R.string.recurrence_weekly),
    MONTHLY(4, R.string.recurrence_monthly),
    YEARLY(5, R.string.recurrence_yearly);

    companion object {
        /**
         * Creates a Recurrence from the supplied id
         * @param id a valid Recurrence id
         */
        fun fromId(id: Int) = values().first { it.ordinal == id }
    }
}