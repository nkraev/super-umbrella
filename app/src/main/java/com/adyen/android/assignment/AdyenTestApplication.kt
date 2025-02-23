package com.adyen.android.assignment

import android.app.Application
import com.adyen.android.assignment.prefs.AppPreferences

class AdyenTestApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    AppPreferences.init(this)
  }
}