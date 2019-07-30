package com.physphil.android.remindme

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.models.Reminder
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class MainActivityViewModelTest {

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

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setUp() {
        initMocks(this)
        viewModel = MainActivityViewModel(repo, scheduler)
    }

    @Test
    fun testEmptyReminderListUpdated() {
        val reminders = emptyList<Reminder>()
        `when`(repo.getActiveReminders()).thenReturn(Flowable.just(reminders))

        viewModel = MainActivityViewModel(repo, scheduler)
        viewModel.reminderListUpdated(reminders)

        viewModel.getSpinnerVisibility().observeForever({ assertEquals(it, false) })
        viewModel.getEmptyVisibility().observeForever({ assertEquals(it, true) })
    }

    @Test
    fun testReminderListUpdated() {
        val reminders = listOf(Reminder(), Reminder())
        `when`(repo.getActiveReminders()).thenReturn(Flowable.just(reminders))

        viewModel = MainActivityViewModel(repo, scheduler)
        viewModel.reminderListUpdated(reminders)

        viewModel.getSpinnerVisibility().observeForever({
            assertEquals(it, false)
        })
        viewModel.getEmptyVisibility().observeForever({
            assertEquals(it, false)
        })
    }

    @Test
    fun testShowReminderList() {
        val reminder1 = Reminder(title = "Reminder 1")
        val reminder2 = Reminder(title = "Reminder 2")
        val reminders = listOf(reminder1, reminder2)
        `when`(repo.getActiveReminders()).thenReturn(Flowable.just(reminders))

        viewModel = MainActivityViewModel(repo, scheduler)
        viewModel.reminderList
                .test()
                .assertValue({ it.size == 2 })
                .assertValue({ it[0].id == reminder1.id })
                .assertValue({ it[1].id == reminder2.id })
    }

    @Test
    fun testConfirmDeleteAllReminders() {
        viewModel.showDeleteAllConfirmationEvent.observeForever({ assertNull(it) })

        viewModel.confirmDeleteAllReminders()
    }

    @Test
    fun testDeleteAllReminders() {
        viewModel.clearNotificationEvent.observeForever({
            assertNull(it)
        })

        viewModel.deleteAllReminders()
        verify(scheduler).cancelAllJobs()
        verify(repo).deleteAllReminders()
    }

    @Test
    fun testConfirmDeleteReminder() {
        viewModel.showDeleteConfirmationEvent.observeForever({ assertNull(it) })

        viewModel.confirmDeleteReminder(Reminder())
    }

    @Test
    fun testDeleteReminder() {
        val reminder = Reminder()
        viewModel.clearNotificationEvent.observeForever({ id ->
            assertEquals(id, reminder.notificationId)
        })

        viewModel.confirmDeleteReminder(reminder)
        viewModel.deleteReminder()
        verify(scheduler).cancelJob(reminder.externalId)
        verify(repo).deleteReminder(reminder)
    }
}