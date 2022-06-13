package com.tutorial.ohmygod.di

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BaseApplication", "the mf has been launched")
    }
}