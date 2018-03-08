package com.physphil.android.remindme

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.Reminder
import com.physphil.android.remindme.util.LiveDataTestUtil
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule



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
        dao.getAllRemindersRx()
                .test()
                .assertValue({ it.isEmpty() })
    }

    @Test
    fun testGetReminderList() {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))
        dao.getAllRemindersRx()
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

        val result = LiveDataTestUtil.getValue(dao.getReminderById(id))
        assertTrue(result.id == id)
        assertTrue(result.title == TITLE)
    }

    @Test
    fun testGetReminderByInvalidId() {
        dao.insertReminder(Reminder())
        dao.insertReminder(Reminder())
        dao.insertReminder(Reminder())

        val result = LiveDataTestUtil.getValue(dao.getReminderById("123"))
        assertNull(result)
    }

    @Test
    fun testDeleteAllReminders() {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))
        dao.insertReminder(Reminder(time = time))

        dao.getAllRemindersRx()
                .test()
                .assertValue({ it.size == 3 })

        dao.deleteAllReminders()
        dao.getAllRemindersRx()
                .test()
                .assertValue( { it.isEmpty() })
    }

    @Test
    fun testDeleteReminder() {
        val reminder = Reminder()
        dao.insertReminder(reminder)

        val result = LiveDataTestUtil.getValue(dao.getReminderById(reminder.id))
        assertNotNull(result)

        dao.deleteReminder(reminder)
        val updatedResult = LiveDataTestUtil.getValue(dao.getReminderById(reminder.id))
        assertNull(updatedResult)
    }

    @Test
    fun testUpdateReminder() {
        val reminder = Reminder(title = TITLE)
        val id = reminder.id
        dao.insertReminder(reminder)

        val result = LiveDataTestUtil.getValue(dao.getReminderById(id))
        assertTrue(result.title == TITLE)
        result.title = NEW_TITLE
        dao.updateReminder(result)

        val updated = LiveDataTestUtil.getValue(dao.getReminderById(id))
        assertTrue(updated.title == NEW_TITLE)
    }

    @Test
    fun testUpdateRecurringReminder() {
        val time = Calendar.getInstance()
        time.timeInMillis = TIME
        val reminder = Reminder(time = time, externalId = EXTERNAL_ID)
        dao.insertReminder(reminder)

        dao.updateRecurringReminder(reminder.id, NEW_EXTERNAL_ID, NEW_TIME)
        val result = LiveDataTestUtil.getValue(dao.getReminderById(reminder.id))
        assertNotNull(result)
        assertTrue(result.externalId == NEW_EXTERNAL_ID)
        assertTrue(result.time.timeInMillis == NEW_TIME)
    }

    @Test
    fun testUpdateNotificationId() {
        val reminder = Reminder(notificationId = NOTIFICATION_ID)
        dao.insertReminder(reminder)

        dao.updateNotificationId(reminder.id, NEW_NOTIFICATION_ID)
        val result = LiveDataTestUtil.getValue(dao.getReminderById(reminder.id))
        assertNotNull(result)
        assertTrue(result.notificationId == NEW_NOTIFICATION_ID)
    }
}