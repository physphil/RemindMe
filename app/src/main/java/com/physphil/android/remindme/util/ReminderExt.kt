package com.physphil.android.remindme.util

import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Reminder
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

val Reminder.displayTime: ViewString
    get() = when {
        time.isNow() -> ViewString.Integer(R.string.reminder_time_now)
        else -> ViewString.String(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
    }

val Reminder.displayDate: ViewString
    get() = when {
        time.isToday() -> ViewString.Integer(R.string.reminder_repeat_today)
        time.isTomorrow() -> ViewString.Integer(R.string.reminder_repeat_tomorrow)
        else -> ViewString.String(time.format(DateTimeFormatter.ofPattern("EEE MMM d, yyyy")))
    }
