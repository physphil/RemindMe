package com.physphil.android.remindme.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.physphil.android.remindme.DATABASE_NAME
import com.physphil.android.remindme.DATABASE_VERSION
import com.physphil.android.remindme.room.entities.Reminder

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
@Database(entities = arrayOf(Reminder::class), version = DATABASE_VERSION)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    // Singleton access pattern borrowed from here
    // https://github.com/googlesamples/android-architecture-components/blob/master/BasicRxJavaSampleKotlin/app/src/main/java/com/example/android/observability/persistence/UsersDatabase.kt
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                        .build()
    }
}