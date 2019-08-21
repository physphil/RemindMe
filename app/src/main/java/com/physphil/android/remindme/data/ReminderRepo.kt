package com.physphil.android.remindme.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.physphil.android.remindme.models.PresetTime
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Repository to handle fetching and saving Reminder data
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderRepo(private val dao: ReminderDao) {

    private val dbScope = CoroutineScope(Dispatchers.Default)

    /**
     * Return a [Reminder] with the given id, wrapped in a [LiveData] object to observe
     * @param id of the Reminder to return
     * @param presetTime the [PresetTime] to initially set the for the Reminder (optional)
     * @return the Reminder wrapped in a [LiveData], or a new empty Reminder if no id is supplied
     */
    fun getReminderByIdOrNew(
        id: String? = null,
        presetTime: PresetTime? = null
    ): LiveData<Reminder> {
        return if (id == null) {
            MutableLiveData<Reminder>().apply {
                value = Reminder(time = presetTime?.time ?: Calendar.getInstance())
            }
        } else {
            dao.getReminderById(id)
        }
    }

    fun getActiveReminders() = dao.getAllReminders()

    /**
     * Insert a new [Reminder] into the database.
     * @param reminder the Reminder to insert.
     */
    fun insertReminder(reminder: Reminder) {
        dbScope.launch { dao.insertReminder(reminder) }
    }

    fun updateReminder(reminder: Reminder) {
        dbScope.launch { dao.updateReminder(reminder) }
    }

    /**
     * When the next event in a recurring [Reminder] is scheduled, call this method to update the Reminder's
     * externalId and time fields for the next scheduled notification
     */
    fun updateRecurringReminder(id: String, newExternalId: Int, newTime: Long) {
        dbScope.launch { dao.updateRecurringReminder(id, newExternalId, newTime) }
    }

    /**
     * Update a [Reminder]'s notificationId, once the Reminder's notification has been shown to the user.
     */
    fun updateNotificationId(id: String, notificationId: Int) {
        dbScope.launch { dao.updateNotificationId(id, notificationId) }
    }

    fun deleteReminder(reminder: Reminder) {
        dbScope.launch { dao.deleteReminder(reminder) }
    }

    fun deleteAllReminders() {
        dbScope.launch { dao.deleteAllReminders() }
    }
}