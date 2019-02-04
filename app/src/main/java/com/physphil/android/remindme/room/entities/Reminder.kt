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
data class Reminder(
    /**
     * The unique id of the Reminder.
     */
    @PrimaryKey
    @ColumnInfo
    var id: String = UUID.randomUUID().toString(),

    /**
     * The Reminder's title.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TITLE)
    var title: String = "",

    /**
     * The Reminder's body content.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TEXT)
    var body: String = "",

    /**
     * A [Calendar] instance representing the Reminder's time.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TIME)
    var time: Calendar = Calendar.getInstance(),

    /**
     * The [Recurrence] of the Reminder (hourly, weekly, etc).
     */
    @ColumnInfo(name = REMINDER_COLUMN_RECURRENCE)
    var recurrence: Recurrence = Recurrence.NONE,

    /**
     * The id of the [ShowNotificationJob] responsible for displaying an Android notification for this Reminder.
     */
    @ColumnInfo(name = REMINDER_COLUMN_EXTERNAL_ID)
    var externalId: Int = 0,

    /**
     * The id of the Android notfication that was displayed to the user for this Reminder. If this field
     * equals 0 a notification has not yet been shown for this Reminder.
     */
    @ColumnInfo(name = REMINDER_COLUMN_NOTIFICATION_ID)
    var notificationId: Int = 0
)