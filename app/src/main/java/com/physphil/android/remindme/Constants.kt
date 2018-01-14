package com.physphil.android.remindme

/**
 * Copyright (c) 2017 Phil Shadlyn
 */

// region Notification Channel ids for Android 8.0+
const val CHANNEL_NOTIFICATIONS = "channel_notifications"
// endregion

// region database information
const val DATABASE_VERSION = 1
const val DATABASE_NAME = "remind_me_database"

const val TABLE_REMINDERS = "reminders"
const val REMINDER_COLUMN_ID = "id"
const val REMINDER_COLUMN_TITLE = "title"
const val REMINDER_COLUMN_TEXT = "text"
const val REMINDER_COLUMN_TIME = "time"
const val REMINDER_COLUMN_RECURRENCE = "recurrence"
const val REMINDER_COLUMN_EXTERNAL_ID = "external_id"
// endregion

// region Android-Job subclass tags
const val TAG_SHOW_NOTIFICATION_JOB = "com.physphil.android.remindme.SHOW_NOTIFICATION_JOB"
// endregion