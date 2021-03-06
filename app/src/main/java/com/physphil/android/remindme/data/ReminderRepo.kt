package com.physphil.android.remindme.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Reminder
import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.util.localDateTimeFromMillis
import com.physphil.android.remindme.util.millis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

/**
 * Repository to handle fetching and saving Reminder data
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderRepo(
    private val dao: ReminderDao,
    private val scheduler: JobRequestScheduler
) {

    private val dbScope = CoroutineScope(Dispatchers.Default)

    /**
     * Return a [Reminder] with the given id, wrapped in a [LiveData] object to observe
     * @param id of the Reminder to return
     * @return the Reminder wrapped in a [LiveData], or a new empty Reminder if no reminder with the
     * supplied id can be found.
     */
    fun getReminder(id: String): LiveData<Reminder> =
        Transformations.map(dao.getReminderById(id)) { entity ->
            entity?.toReminderModel() ?: Reminder()
        }

    fun getActiveReminders(): LiveData<List<Reminder>> =
        Transformations.map(dao.getActiveReminders()) { entities ->
            entities.map {
                it.toReminderModel()
            }
        }

    fun getOldReminderCount(): LiveData<Int> = dao.getOldReminderCount()

    fun getOldestReminderDate(): LiveData<LocalDateTime> =
        Transformations.map(dao.getOldestReminderTime()) { millis ->
            localDateTimeFromMillis(millis)
        }

    fun addReminder(reminder: Reminder) {
        dbScope.launch {
            dao.insertReminder(
                reminder = reminder.schedule().toReminderEntity()
            )
        }
    }

    fun updateReminder(reminder: Reminder) {
        dbScope.launch {
            scheduler.cancelJob(reminder.externalId)
            dao.updateReminder(reminder.schedule().toReminderEntity())
        }
    }

    /**
     * When the next event in a recurring [Reminder] is scheduled, call this method to update the Reminder's
     * externalId and time fields for the next scheduled notification
     */
    fun updateRecurringReminder(reminder: Reminder) {
        dbScope.launch {
            val updatedReminder = reminder.schedule()
            dao.updateRecurringReminder(updatedReminder.id, updatedReminder.externalId, updatedReminder.time.millis)
        }
    }

    /**
     * Update a [Reminder]'s notificationId, once the Reminder's notification has been shown to the user.
     */
    fun updateNotificationId(id: String, notificationId: Int) {
        dbScope.launch { dao.updateNotificationId(id, notificationId) }
    }

    fun deleteReminder(reminder: Reminder) {
        dbScope.launch {
            scheduler.cancelJob(reminder.externalId)
            dao.deleteReminder(reminder.toReminderEntity())
        }
    }

    fun deleteAllReminders() {
        dbScope.launch {
            scheduler.cancelAllJobs()
            dao.deleteAllReminders()
        }
    }

    private fun Reminder.schedule(): Reminder =
        this.copy(
            externalId = scheduler.scheduleShowNotificationJob(this)
        )
}