package com.physphil.android.remindme.room.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.physphil.android.remindme.*
import com.physphil.android.remindme.models.Recurrence
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val NEW_REMINDER_ID = 0

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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = REMINDER_COLUMN_ID)
    var id: Int = 0

    fun getDisplayTime() = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(time.time)

    fun getDisplayDate() = SimpleDateFormat.getDateInstance().format(time.time)

    fun isNewReminder() = id == NEW_REMINDER_ID
}