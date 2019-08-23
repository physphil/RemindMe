package com.physphil.android.remindme.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import kotlin.math.abs

/**
 * Copyright (c) 2018 Phil Shadlyn
 */

fun LocalDateTime.isToday(): Boolean {
    val today = LocalDateTime.now()
    return (this.year == today.year
        && this.dayOfYear == today.dayOfYear)
}

fun LocalDateTime.isTomorrow(): Boolean {
    val today = LocalDateTime.now()
    return (this.year == today.year
        && this.dayOfYear == today.plusDays(1).dayOfYear)
}

fun LocalDateTime.isNow(): Boolean {
    val now = System.currentTimeMillis()
    return abs(this.millis - now) < (1000 * 5)  // Considered "now" if within 5 seconds of current time
}

fun LocalDateTime.isInPast(): Boolean = this.millis < System.currentTimeMillis()

fun LocalDateTime.endOfDay(): LocalDateTime =
    this.withHour(17)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .apply {
            if (isInPast()) advanceDay()
        }

fun LocalDateTime.tonight(): LocalDateTime =
    this.withHour(19)
        .withMinute(30)
        .withSecond(0)
        .withNano(0)
        .apply {
            if (isInPast()) advanceDay()
        }

fun LocalDateTime.tomorrowMorning(): LocalDateTime =
    this.withHour(7)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .advanceDay()

private fun LocalDateTime.advanceDay(): LocalDateTime = plusDays(1)

val LocalDateTime.millis: Long
    get() = ZonedDateTime.of(this, ZoneId.systemDefault()).toInstant().toEpochMilli()

fun localDateTimeFromMillis(millis: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())