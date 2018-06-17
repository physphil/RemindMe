package com.physphil.android.remindme

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.Reminder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

private const val TITLE = "Watch The Wire"
private const val NEW_TITLE = "Watch Seinfeld"
private const val EXTERNAL_ID = 3
private const val NEW_EXTERNAL_ID = 4
private const val TIME = 5000L
private const val NEW_TIME = 10000L
private const val NOTIFICATION_ID = 9
private const val NEW_NOTIFICATION_ID = 10

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
@RunWith(AndroidJUnit4::class)
class ReminderDaoTest {

    private lateinit var dao: ReminderDao
    private lateinit var db: AppDatabase

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.reminderDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testGetEmptyReminderList() {
        dao.getAllReminders()
                .test()
                .assertValue({ it.isEmpty() })
    }

    @Test
    fun testGetReminderList() {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))
        dao.getAllReminders()
                .test()
                .assertValue({ it.size == 2 })
    }

    @Test
    fun testGetReminderById() {
        val reminder = Reminder(title = TITLE)
        val id = reminder.id
        dao.insertReminder(reminder)
        dao.insertReminder(Reminder())
        dao.insertReminder(Reminder())

        dao.getReminderById(id)
                .test()
                .assertValue({ it.id == id })
                .assertValue({ it.title == TITLE })
    }

    @Test
    fun testGetReminderByInvalidId() {
        dao.insertReminder(Reminder())
        dao.insertReminder(Reminder())
        dao.insertReminder(Reminder())

        dao.getReminderById("123")
                .test()
                .assertNoValues()
    }

    @Test
    fun testDeleteAllReminders() {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))

        dao.getAllReminders()
                .test()
                .assertValue({ it.size == 3 })

        dao.deleteAllReminders()
        dao.getAllReminders()
                .test()
                .assertValue( { it.isEmpty() })
    }

    @Test
    fun testDeleteReminder() {
        val reminder = Reminder()
        dao.insertReminder(reminder)

        dao.getReminderById(reminder.id)
                .test()
                .assertValueCount(1)

        dao.deleteReminder(reminder)
        dao.getReminderById(reminder.id)
                .test()
                .assertNoValues()
    }

    @Test
    fun testUpdateReminder() {
        val reminder = Reminder(title = TITLE)
        val id = reminder.id
        dao.insertReminder(reminder)

        dao.getReminderById(id)
                .test()
                .assertValue({ it.title == TITLE })

        reminder.title = NEW_TITLE
        dao.updateReminder(reminder)

        dao.getReminderById(id)
                .test()
                .assertValue({ it.title == NEW_TITLE })
    }

    @Test
    fun testUpdateRecurringReminder() {
        val time = Calendar.getInstance()
        time.timeInMillis = TIME
        val reminder = Reminder(time = time, externalId = EXTERNAL_ID)
        dao.insertReminder(reminder)

        dao.updateRecurringReminder(reminder.id, NEW_EXTERNAL_ID, NEW_TIME)
        dao.getReminderById(reminder.id)
                .test()
                .assertValueCount(1)
                .assertValue({ it.externalId == NEW_EXTERNAL_ID })
                .assertValue({ it.time.timeInMillis == NEW_TIME })
    }

    @Test
    fun testUpdateNotificationId() {
        val reminder = Reminder(notificationId = NOTIFICATION_ID)
        dao.insertReminder(reminder)

        dao.updateNotificationId(reminder.id, NEW_NOTIFICATION_ID)
        dao.getReminderById(reminder.id)
                .test()
                .assertValueCount(1)
                .assertValue({ it.notificationId == NEW_NOTIFICATION_ID })
    }
}