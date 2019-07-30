package com.physphil.android.remindme.models

import com.physphil.android.remindme.room.entities.ReminderEntity
import java.util.Calendar
import java.util.UUID

/**
 * Represents a Reminder to be shown to the user.
 */
data class Reminder(
    /**
     * The unique id of the Reminder.
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * The Reminder's title.
     */
    var title: String = "",

    /**
     * The Reminder's body content.
     */
    var body: String = "",

    /**
     * A [Calendar] instance representing the Reminder's time.
     */
    var time: Calendar = Calendar.getInstance(),

    /**
     * The [Recurrence] of the Reminder (hourly, weekly, etc).
     */
    var recurrence: Recurrence = Recurrence.NONE,

    /**
     * The id of the [ShowNotificationJob] responsible for displaying an Android notification for this Reminder.
     */
    var externalId: Int = 0,

    /**
     * The id of the Android notfication that was displayed to the user for this Reminder. If this field
     * equals 0 a notification has not yet been shown for this Reminder.
     */
    var notificationId: Int = 0
) {
    fun toReminderEntity(): ReminderEntity = ReminderEntity(
        id = id,
        title = title,
        body = body,
        time = time,
        recurrence = recurrence,
        externalId = externalId,
        notificationId = notificationId
    )
}