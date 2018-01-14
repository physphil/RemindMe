package com.physphil.android.remindme.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.NEW_REMINDER_ID
import com.physphil.android.remindme.room.entities.Reminder

/**
 * Repository to handle fetching and saving Reminder data
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderRepo(private val dao: ReminderDao) {

    fun getReminderById(id: Int): LiveData<Reminder> {
        return if (id == NEW_REMINDER_ID) {
            val data = MutableLiveData<Reminder>()
            data.value = Reminder()
            data
        }
        else {
            dao.getReminderById(id)
        }
    }

    fun insertReminder(reminder: Reminder) {
        Thread(Runnable {
            dao.insertReminder(reminder)
        }).start()
    }

    fun updateReminder(reminder: Reminder) {
        Thread(Runnable {
            dao.updateReminder(reminder)
        }).start()
    }

    fun updateRecurringReminder(id: Int, newExternalId: Int, newTime: Long) {
//        Thread(Runnable {
            dao.updateRecurringReminder(id, newExternalId, newTime)
//        }).start()
    }
}