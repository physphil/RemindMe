package com.physphil.android.remindme.data

import com.physphil.android.remindme.room.ReminderDao
import com.physphil.android.remindme.room.entities.Reminder
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Repository to handle fetching and saving Reminder data
 *
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderRepo(private val dao: ReminderDao) {

    /**
     * Return a [Reminder] with the given id, wrapped in a [Flowable] object to observe
     * @param id of the Reminder to return
     * @return the Reminder wrapped in a [Flowable], or a new empty Reminder if no id is supplied
     */
    fun getReminderByIdOrNew(id: String? = null): Flowable<Reminder> {
        return if (id == null) {
            Flowable.just(Reminder())
        } else {
            dao.getReminderById(id)
        }
    }

    fun getActiveReminders() = dao.getAllReminders()

    /**
     * Insert a new [Reminder] into the database. Returns a [Disposable] that can be used to cancel the subscription.
     * @param reminder the Reminder to insert.
     * @return a disposable for the subscription
     */
    fun insertReminder(reminder: Reminder): Disposable {
        return doDatabaseOperation { dao.insertReminder(reminder) }
    }

    fun updateReminder(reminder: Reminder): Disposable {
        return doDatabaseOperation { dao.updateReminder(reminder) }
    }

    /**
     * When the next event in a recurring [Reminder] is scheduled, call this method to update the Reminder's
     * externalId and time fields for the next scheduled notification
     */
    fun updateRecurringReminder(id: String, newExternalId: Int, newTime: Long) {
        doDatabaseOperation { dao.updateRecurringReminder(id, newExternalId, newTime) }
    }

    /**
     * Update a [Reminder]'s notificationId, once the Reminder's notification has been shown to the user.
     */
    fun updateNotificationId(id: String, notificationId: Int) {
        doDatabaseOperation { dao.updateNotificationId(id, notificationId) }
    }

    fun deleteReminder(reminder: Reminder) {
        doDatabaseOperation { dao.deleteReminder(reminder) }
    }

    fun deleteAllReminders() {
        doDatabaseOperation { dao.deleteAllReminders() }
    }

    /**
     * Convenience method to perform a database operation off the main thread.
     * @param action the database operation to perform
     * @return a [Disposable] representing the operation, that can be unsubscribed from if required
     */
    private fun doDatabaseOperation(action: () -> Unit): Disposable {
        return Completable.fromAction(action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}