package com.physphil.android.remindme.room;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.physphil.android.remindme.room.entities.Reminder;

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
@android.arch.persistence.room.Dao
public interface daojava {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertReminders(Reminder...reminders);

    @Query("SELECT * FROM reminders")
    public Reminder[] getAllReminders();

}
