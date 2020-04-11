package com.physphil.android.remindme.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.data.ReminderRepo
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat

class StatsViewModel(private val repo: ReminderRepo) : ViewModel() {

    val reminderCountLiveData: LiveData<String> =
        Transformations.map(repo.getOldReminderCount()) {
            DecimalFormat.getIntegerInstance().format(it)
        }

    val oldestReminderDateLiveData: LiveData<String> =
        Transformations.map(repo.getOldestReminderDate()) { dateTime ->
            dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        }

    class Factory(private val repo: ReminderRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                return StatsViewModel(repo) as T
            }

            throw IllegalArgumentException("Cannot instantiate ViewModel class with those arguments")
        }
    }
}