package com.physphil.android.remindme.models

import com.physphil.android.remindme.util.endOfDay
import com.physphil.android.remindme.util.tomorrowMorning
import com.physphil.android.remindme.util.tonight
import org.threeten.bp.LocalDateTime

/**
 * Represents a common time that a user might select as their [Reminder]'s time.
 */
sealed class PresetTime(
    val id: Int,
    val time: LocalDateTime
) {
    class EndOfDay : PresetTime(ID_EOD, LocalDateTime.now().endOfDay())
    class Tonight : PresetTime(ID_TONIGHT, LocalDateTime.now().tonight())
    class TomorrowMorning : PresetTime(ID_TOMORROW_MORNING, LocalDateTime.now().tomorrowMorning())

    companion object {
        const val ID_UNKNOWN = -1
        const val ID_EOD = 0
        const val ID_TONIGHT = 1
        const val ID_TOMORROW_MORNING = 2

        fun fromId(id: Int): PresetTime? =
            when (id) {
                ID_EOD -> EndOfDay()
                ID_TONIGHT -> Tonight()
                ID_TOMORROW_MORNING -> TomorrowMorning()
                else -> null
            }
    }
}