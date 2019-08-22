package com.physphil.android.remindme.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.physphil.android.remindme.DATABASE_NAME
import com.physphil.android.remindme.DATABASE_VERSION
import com.physphil.android.remindme.room.entities.ReminderEntity

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
@Database(entities = [ReminderEntity::class], version = DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}