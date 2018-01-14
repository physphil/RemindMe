package com.physphil.android.remindme.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.physphil.android.remindme.REMINDER_COLUMN_EXTERNAL_ID
import com.physphil.android.remindme.REMINDER_COLUMN_ID
import com.physphil.android.remindme.REMINDER_COLUMN_TIME
import com.physphil.android.remindme.TABLE_REMINDERS
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

    @Query("SELECT * FROM $TABLE_REMINDERS")
    fun getAllReminders(): LiveData<Array<Reminder>>

    @Query("SELECT * FROM $TABLE_REMINDERS WHERE id = :id")
    fun getReminderById(id: String): LiveData<Reminder>

    @Query("UPDATE $TABLE_REMINDERS " +
            "SET $REMINDER_COLUMN_EXTERNAL_ID = :newExternalId, $REMINDER_COLUMN_TIME = :newTime " +
            "WHERE $REMINDER_COLUMN_ID = :id")
    fun updateRecurringReminder(id: String, newExternalId: Int, newTime: Long)
}