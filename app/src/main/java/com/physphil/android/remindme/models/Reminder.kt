package com.physphil.android.remindme.models

import android.os.Parcelable
import com.physphil.android.remindme.room.entities.ReminderEntity
import com.physphil.android.remindme.util.millis
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime
import java.util.UUID

/**
 * Represents a Reminder to be shown to the user.
 */
@Parcelize
data class Reminder(
    /**
     * The unique id of the Reminder.
     */
    val id: String = UUID.randomUUID().toString(),

    /**
     * The Reminder's title.
     */
    val title: String = "",

    /**
     * The Reminder's body content.
     */
    val body: String = "",

    /**
     * A [LocalDateTime] instance representing the Reminder's time.
     */
    val time: LocalDateTime = LocalDateTime.now(),

    /**
     * The [Recurrence] of the Reminder (hourly, weekly, etc).
     */
    val recurrence: Recurrence = Recurrence.NONE,

    /**
     * The id of the [ShowNotificationJob] responsible for displaying an Android notification for this Reminder.
     */
    val externalId: Int = 0,

    /**
     * The id of the Android notfication that was displayed to the user for this Reminder. If this field
     * equals 0 a notification has not yet been shown for this Reminder.
     */
    val notificationId: Int = 0
) : Parcelable {
    fun toReminderEntity(): ReminderEntity = ReminderEntity(
        id = id,
        title = title,
        body = body,
        time = time.millis,
        recurrence = recurrence.id,
        externalId = externalId,
        notificationId = notificationId
    )
}