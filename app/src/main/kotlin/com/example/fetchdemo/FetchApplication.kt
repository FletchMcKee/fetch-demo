package com.example.fetchdemo

import android.app.Application
import android.os.Build
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FetchApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    setupStrictMode()
  }

  private fun setupStrictMode() {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
          .detectAll()
          .penaltyLog()
          .build(),
      )
      StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectActivityLeaks()
          .detectLeakedClosableObjects()
          .detectLeakedRegistrationObjects()
          .detectFileUriExposure()
          .detectCleartextNetwork()
          .detectContentUriWithoutPermission()
          .apply {
            if (Build.VERSION.SDK_INT >= 29) {
              detectCredentialProtectedWhileLocked()
            }
            if (Build.VERSION.SDK_INT >= 31) {
              detectIncorrectContextUse()
              detectUnsafeIntentLaunch()
            }
          }
          .penaltyLog()
          .build(),
      )
    }
  }
}
