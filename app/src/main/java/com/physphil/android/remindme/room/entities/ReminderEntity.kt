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
import com.physphil.android.remindme.models.Reminder
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Entity to store Reminder objects in Room
 *
 * Copyright (c) 2017 Phil Shadlyn
 */
@Entity(tableName = TABLE_REMINDERS)
data class ReminderEntity(
    /**
     * The unique id of the Reminder.
     */
    @PrimaryKey
    @ColumnInfo
    var id: String,

    /**
     * The Reminder's title.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TITLE)
    var title: String,

    /**
     * The Reminder's body content.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TEXT)
    var body: String,

    /**
     * The Reminder's time in millis.
     */
    @ColumnInfo(name = REMINDER_COLUMN_TIME)
    var time: Long,

    /**
     * The [id]][Recurrence.id] of the Reminder's recurrence (hourly, weekly, etc).
     */
    @ColumnInfo(name = REMINDER_COLUMN_RECURRENCE)
    var recurrence: Int,

    /**
     * The id of the [ShowNotificationJob] responsible for displaying an Android notification for this Reminder.
     */
    @ColumnInfo(name = REMINDER_COLUMN_EXTERNAL_ID)
    var externalId: Int,

    /**
     * The id of the Android notfication that was displayed to the user for this Reminder. If this field
     * equals 0 a notification has not yet been shown for this Reminder.
     */
    @ColumnInfo(name = REMINDER_COLUMN_NOTIFICATION_ID)
    var notificationId: Int
) {
    fun toReminderModel(): Reminder = Reminder(
        id = id,
        title = title,
        body = body,
        time = fromMillis(this@ReminderEntity.time),
        recurrence = Recurrence.fromId(recurrence),
        externalId = externalId,
        notificationId = notificationId
    )

    private fun fromMillis(millis: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
}