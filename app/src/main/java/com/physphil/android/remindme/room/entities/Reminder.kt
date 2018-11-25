package com.physphil.android.remindme.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.physphil.android.remindme.REMINDER_COLUMN_EXTERNAL_ID
import com.physphil.android.remindme.REMINDER_COLUMN_NOTIFICATION_ID
import com.physphil.android.remindme.REMINDER_COLUMN_RECURRENCE
import com.physphil.android.remindme.REMINDER_COLUMN_TEXT
import com.physphil.android.remindme.REMINDER_COLUMN_TIME
import com.physphil.android.remindme.REMINDER_COLUMN_TITLE
import com.physphil.android.remindme.TABLE_REMINDERS
import com.physphil.android.remindme.models.Recurrence
import java.util.Calendar
import java.util.UUID

/**
 * Entity to store Reminder objects in Room
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
@Entity(tableName = TABLE_REMINDERS)
data class Reminder(@PrimaryKey @ColumnInfo var id: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = REMINDER_COLUMN_TITLE) var title: String = "",
        @ColumnInfo(name = REMINDER_COLUMN_TEXT) var body: String = "",
        @ColumnInfo(name = REMINDER_COLUMN_TIME) var time: Calendar = Calendar.getInstance(),
        @ColumnInfo(name = REMINDER_COLUMN_RECURRENCE) var recurrence: Recurrence = Recurrence.NONE,
        @ColumnInfo(name = REMINDER_COLUMN_EXTERNAL_ID) var externalId: Int = 0,
        @ColumnInfo(name = REMINDER_COLUMN_NOTIFICATION_ID) var notificationId: Int = 0)