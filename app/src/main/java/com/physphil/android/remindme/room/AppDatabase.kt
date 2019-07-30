package com.physphil.android.remindme.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.physphil.android.remindme.DATABASE_VERSION
import com.physphil.android.remindme.room.entities.ReminderEntity

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
@Database(entities = arrayOf(ReminderEntity::class), version = DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}