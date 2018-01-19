package com.physphil.android.remindme.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.physphil.android.remindme.*
import com.physphil.android.remindme.room.entities.Reminder

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
    fun getAllReminders(time: Long): LiveData<List<Reminder>>

    @Query("SELECT * FROM $TABLE_REMINDERS " +
            "WHERE $REMINDER_COLUMN_ID = :id")
    fun getReminderById(id: String): LiveData<Reminder>

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