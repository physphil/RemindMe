package com.physphil.android.remindme.util

import android.annotation.SuppressLint
import android.content.Context
import com.physphil.android.remindme.R
import com.physphil.android.remindme.models.Reminder
import java.text.DateFormat
import java.text.SimpleDateFormat

fun Reminder.getDisplayTime(context: Context): String = when {
    time.isNow() -> context.getString(R.string.reminder_time_now)
    else -> SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(time.millis)
}

@SuppressLint("SimpleDateFormat")
fun Reminder.getDisplayDate(context: Context): String = when {
    time.isToday() -> context.getString(R.string.reminder_repeat_today)
    time.isTomorrow() -> context.getString(R.string.reminder_repeat_tomorrow)
    else -> SimpleDateFormat("EEE MMM d, yyyy").format(time.millis)
}

