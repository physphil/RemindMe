package com.physphil.android.remindme.room

import android.arch.persistence.room.TypeConverter
import com.physphil.android.remindme.models.Recurrence
import java.util.*

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
class Converters {

    @TypeConverter
    fun fromRecurrence(recurrence: Recurrence): Int = recurrence.id

    @TypeConverter
    fun fromId(id: Int): Recurrence = Recurrence.fromId(id)

    @TypeConverter
    fun fromCalendar(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun fromMillis(millis: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar
    }
}