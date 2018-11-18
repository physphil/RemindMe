package com.physphil.android.remindme.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.physphil.android.remindme.REMINDER_COLUMN_EXTERNAL_ID
import com.physphil.android.remindme.REMINDER_COLUMN_ID
import com.physphil.android.remindme.REMINDER_COLUMN_NOTIFICATION_ID
import com.physphil.android.remindme.REMINDER_COLUMN_TIME
import com.physphil.android.remindme.TABLE_REMINDERS
import com.physphil.android.remindme.room.entities.Reminder
import io.reactivex.Flowable

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder)

    @Update
    fun updateReminder(reminder: Reminder)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM $TABLE_REMINDERS " +
            "WHERE $REMINDER_COLUMN_TIME > :time " +
            "ORDER BY $REMINDER_COLUMN_TIME ASC")
    fun getAllReminders(time: Long = System.currentTimeMillis()): Flowable<List<Reminder>>

    @Query("SELECT * FROM $TABLE_REMINDERS " +
            "WHERE $REMINDER_COLUMN_ID = :id")
    fun getReminderById(id: String): Flowable<Reminder>

    @Query("UPDATE $TABLE_REMINDERS " +
            "SET $REMINDER_COLUMN_EXTERNAL_ID = :newExternalId, $REMINDER_COLUMN_TIME = :newTime " +
            "WHERE $REMINDER_COLUMN_ID = :id")
    fun updateRecurringReminder(id: String, newExternalId: Int, newTime: Long)

    @Query("UPDATE $TABLE_REMINDERS " +
            "SET $REMINDER_COLUMN_NOTIFICATION_ID = :notificationId " +
            "WHERE $REMINDER_COLUMN_ID = :id")
    fun updateNotificationId(id: String, notificationId: Int)

    @Query("DELETE FROM $TABLE_REMINDERS")
    fun deleteAllReminders()
}