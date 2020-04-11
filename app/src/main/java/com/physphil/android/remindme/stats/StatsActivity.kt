package com.physphil.android.remindme.stats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.physphil.android.remindme.R
import com.physphil.android.remindme.inject.Injector
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : AppCompatActivity() {

    private lateinit var viewModel: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Setup viewmodel
        val factory = StatsViewModel.Factory(Injector.provideReminderRepo(this))
        viewModel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)
        viewModel.bind(this)
    }

    private fun StatsViewModel.bind(lifecycleOwner: LifecycleOwner) {
        reminderCountLiveData.observe(lifecycleOwner, Observer {
            statsTotalView.text = it.toString()
        })
    }
}
