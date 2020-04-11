package com.physphil.android.remindme

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.physphil.android.remindme.room.AppDatabase
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.ReminderEntity
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
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
    fun testGetEmptyReminderList() = runBlocking {
        val reminders = dao.getActiveReminders()
        assertTrue(reminders.value!!.isEmpty())
    }

    @Test
    fun testGetReminderList() = runBlocking {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(ReminderEntity(time = time))
        dao.insertReminder(ReminderEntity(time = time))
        val reminders = dao.getActiveReminders()
        assertTrue(reminders.value!!.size == 2)
    }

    @Test
    fun testGetReminderById() = runBlocking {
        val reminder = ReminderEntity(title = TITLE)
        val id = reminder.id
        dao.insertReminder(reminder)
        dao.insertReminder(ReminderEntity())
        dao.insertReminder(ReminderEntity())

        val actual = dao.getReminderById(id)
        assertTrue(actual.value!!.id == id)
        assertTrue(actual.value!!.title == TITLE)
    }

    @Test
    fun testGetReminderByInvalidId() = runBlocking {
        dao.insertReminder(ReminderEntity())
        dao.insertReminder(ReminderEntity())
        dao.insertReminder(ReminderEntity())

        val reminder = dao.getReminderById("123")
        assertNull(reminder.value)
    }

    @Test
    fun testDeleteAllReminders() = runBlocking {
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis() + 60000 * 5  // set each reminder 5 minutes in future
        dao.insertReminder(ReminderEntity(time = time))
        dao.insertReminder(ReminderEntity(time = time))
        dao.insertReminder(ReminderEntity(time = time))

        val firstList = dao.getActiveReminders()
        assertTrue(firstList.value!!.size == 3)

        dao.deleteAllReminders()
        val secondList = dao.getActiveReminders()
        assertTrue(secondList.value!!.isEmpty())
    }

    @Test
    fun testDeleteReminder() = runBlocking {
        val reminder = ReminderEntity()
        dao.insertReminder(reminder)

        val actual = dao.getReminderById(reminder.id)
        assertNotNull(actual.value)

        dao.deleteReminder(reminder)
        val nullActual = dao.getReminderById(reminder.id)
        assertNull(nullActual)
    }

    @Test
    fun testUpdateReminder() = runBlocking {
        val reminder = ReminderEntity(title = TITLE)
        val id = reminder.id
        dao.insertReminder(reminder)

        val actual = dao.getReminderById(id)
        assertTrue(actual.value!!.title == TITLE)

        reminder.title = NEW_TITLE
        dao.updateReminder(reminder)

        val updatedActual = dao.getReminderById(id)
        assertTrue(updatedActual.value!!.title == NEW_TITLE)
    }

    @Test
    fun testUpdateRecurringReminder() = runBlocking {
        val time = Calendar.getInstance()
        time.timeInMillis = TIME
        val reminder = ReminderEntity(time = time, externalId = EXTERNAL_ID)
        dao.insertReminder(reminder)

        dao.updateRecurringReminder(reminder.id, NEW_EXTERNAL_ID, NEW_TIME)
        val actual = dao.getReminderById(reminder.id)
        with(actual.value!!) {
            assertTrue(externalId == NEW_EXTERNAL_ID)
            assertTrue(time.timeInMillis == NEW_TIME)
        }
    }

    @Test
    fun testUpdateNotificationId() = runBlocking {
        val reminder = ReminderEntity(notificationId = NOTIFICATION_ID)
        dao.insertReminder(reminder)

        dao.updateNotificationId(reminder.id, NEW_NOTIFICATION_ID)
        val actual = dao.getReminderById(reminder.id)
        assertTrue(actual.value!!.notificationId == NEW_NOTIFICATION_ID)
    }
}