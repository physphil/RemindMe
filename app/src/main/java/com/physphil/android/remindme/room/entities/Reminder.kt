package com.physphil.android.remindme.room.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import com.physphil.android.remindme.*
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.util.isToday
import com.physphil.android.remindme.util.isTomorrow
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Entity to store Reminder objects in Room
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
@Entity(tableName = TABLE_REMINDERS)
data class Reminder(@ColumnInfo(name = REMINDER_COLUMN_TITLE) var title: String = "",
                    @ColumnInfo(name = REMINDER_COLUMN_TEXT) var body: String = "",
                    @ColumnInfo(name = REMINDER_COLUMN_TIME) var time: Calendar = Calendar.getInstance(),
                    @ColumnInfo(name = REMINDER_COLUMN_RECURRENCE) var recurrence: Recurrence = Recurrence.NONE,
                    @ColumnInfo(name = REMINDER_COLUMN_EXTERNAL_ID) var externalId: Int = 0) {

    @PrimaryKey
    @ColumnInfo(name = REMINDER_COLUMN_ID)
    var id: String = UUID.randomUUID().toString()

    fun getDisplayTime(): String = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(time.time)

    fun getDisplayDate(context: Context): String = when {
        time.isToday() -> context.getString(R.string.reminder_repeat_today)
        time.isTomorrow() -> context.getString(R.string.reminder_repeat_tomorrow)
        else -> SimpleDateFormat.getDateInstance().format(time.time)
    }
}