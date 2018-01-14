package com.physphil.android.remindme

import com.facebook.stetho.Stetho

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class RemindMeDebugApplication : RemindMeApplication() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}