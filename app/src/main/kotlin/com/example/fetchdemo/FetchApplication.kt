package com.example.fetchdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FetchApplication : Application() {
  override fun onCreate() {
    super.onCreate()
  }
}
