package com.physphil.android.remindme

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.physphil.android.remindme.data.ReminderRepo
import com.physphil.android.remindme.job.JobRequestScheduler
import com.physphil.android.remindme.room.entities.Reminder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
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
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun testDeleteAllReminders() {
        viewModel.deleteAllReminders()
        verify(scheduler).cancelAllJobs()
        verify(repo).deleteAllReminders()
    }

    @Test
    fun testDeleteReminder() {
        val reminder = Reminder()
        viewModel.confirmDeleteReminder(reminder)
        viewModel.deleteReminder()
        verify(scheduler).cancelJob(reminder.externalId)
        verify(repo).deleteReminder(reminder)
    }
}