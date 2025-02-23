package com.adyen.android.assignment

import android.app.Application
import com.adyen.android.assignment.di.ServiceLocator
import com.adyen.android.assignment.prefs.AppPreferences

class AdyenTestApplication : Application() {
  override fun onCreate() {
    ServiceLocator.init(this)
    AppPreferences.init(this)
    super.onCreate()
  }
}