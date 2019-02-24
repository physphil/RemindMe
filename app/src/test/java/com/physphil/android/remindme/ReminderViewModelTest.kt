package com.physphil.android.remindme

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Recurrence
import com.physphil.android.remindme.reminders.ReminderViewModel
import com.physphil.android.remindme.room.entities.Reminder
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import java.util.Calendar

private const val TITLE = "Buy cucumbers"
private const val BODY = "And some limes"
private const val TIME = "9:21 pm"
private const val DATE = "March 8, 2018"
private val RECURRENCE = Recurrence.WEEKLY.id
private const val EXTERNAL_ID = 9
private const val NEW_EXTERNAL_ID = 99
private const val NOTIFICATION_ID = 8

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderViewModelTest {

    /*
     * This is required to test LiveData. When setting values Android checks to see what thread the call is made from,
     * and this rule returns the required result to avoid an NPE
     */
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: ReminderRepo

    @Mock
    private lateinit var scheduler: JobRequestScheduler

    @Mock
    private lateinit var observer: Observer<Any>

    private lateinit var viewModel: ReminderViewModel
    private val reminder = Reminder()

    @Before
    fun setUp() {
        initMocks(this)

        // Setup reminder to test in viewmodel
        reminder.title = TITLE
        reminder.body = BODY
        reminder.externalId = EXTERNAL_ID
        reminder.notificationId = NOTIFICATION_ID
        `when`(repo.getReminderByIdOrNew(reminder.id)).thenReturn(Flowable.just(reminder))
        `when`(repo.getReminderByIdOrNew()).thenReturn(Flowable.just(reminder))

        `when`(scheduler.scheduleShowNotificationJob(reminder.time.timeInMillis,
                reminder.id, reminder.title, reminder.body, reminder.recurrence.id)).thenReturn(NEW_EXTERNAL_ID)

        viewModel = ReminderViewModel(reminder.id, null, repo, scheduler)
        viewModel.reminder = reminder
    }

    @Test
    fun testToolbarTitleNewReminder() {
        viewModel = ReminderViewModel(repo = repo, scheduler = scheduler)
        assert(viewModel.getToolbarTitle().value == R.string.title_add_reminder)
    }

    @Test
    fun testToolbarTitleEditReminder() {
        assert(viewModel.getToolbarTitle().value == R.string.title_edit_reminder)
    }

    @Test
    fun testUpdateTitle() {
        viewModel.updateTitle(TITLE)
        assert(viewModel.reminder.title == TITLE)
    }

    @Test
    fun testUpdateBody() {
        viewModel.updateBody(BODY)
        assert(viewModel.reminder.body == BODY)
    }

    @Test
    fun testUpdateRecurrence() {
        val recurrence = Recurrence.fromId(RECURRENCE)
        viewModel.updateRecurrence(recurrence)
        assert(viewModel.reminder.recurrence == recurrence)
        assert(viewModel.getReminderRecurrence().value == recurrence.displayString)
    }

    @Test
    fun testSaveNewReminder() {
        viewModel = ReminderViewModel(repo = repo, scheduler = scheduler)
        viewModel.reminder = reminder
        viewModel.saveReminder()
        assert(viewModel.reminder.time.get(Calendar.SECOND) == 0)
        assert(viewModel.reminder.time.get(Calendar.MILLISECOND) == 0)
        assert(viewModel.reminder.externalId == NEW_EXTERNAL_ID)
        verify(repo).insertReminder(reminder)
    }

    @Test
    fun testSaveExistingReminder() {
        viewModel.saveReminder()
        assert(viewModel.reminder.time.get(Calendar.SECOND) == 0)
        assert(viewModel.reminder.time.get(Calendar.MILLISECOND) == 0)
        verify(scheduler).cancelJob(EXTERNAL_ID)
        assert(viewModel.reminder.externalId == NEW_EXTERNAL_ID)
        verify(repo).updateReminder(reminder)
    }

    @Test
    fun testConfirmDeleteReminder() {
        viewModel.confirmDeleteEvent.observeForever(observer as Observer<Void>)

        viewModel.confirmDeleteReminder()
        verify(observer).onChanged(null)
    }

    @Test
    fun testDeleteReminder() {
        viewModel.closeActivityEvent.observeForever(observer as Observer<Void>)

        viewModel.deleteReminder()
        assert(viewModel.clearNotificationEvent.value == NOTIFICATION_ID)
        verify(scheduler).cancelJob(EXTERNAL_ID)
        verify(repo).deleteReminder(reminder)
        verify(observer).onChanged(null)
    }
}