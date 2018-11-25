package com.physphil.android.remindme

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
abstract class BaseActivity : AppCompatActivity() {

    fun setToolbarTitle(@StringRes title: Int) {
        supportActionBar?.setTitle(title)
    }

    fun setHomeArrowBackNavigation() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}