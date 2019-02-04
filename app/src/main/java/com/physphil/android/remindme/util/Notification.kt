package com.physphil.android.remindme.util

import kotlin.random.Random

object Notification {

    /**
     * Returns a random ID for a notification to be displayed for a Reminder.
     */
    val nextId: Int
        get() = Random.nextInt()
}