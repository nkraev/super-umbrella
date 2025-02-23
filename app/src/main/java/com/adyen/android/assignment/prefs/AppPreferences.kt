package com.adyen.android.assignment.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

object AppPreferences {
  private var weakCtx: WeakReference<Context> = WeakReference(null)
  private val apiTimestampKey = longPreferencesKey("last_fetched_api_timestamp")

  fun init(context: Context) {
    weakCtx.clear()
    weakCtx = WeakReference(context)
  }

  suspend fun getLastFetchedApiTimestamp(): Long {
    val data = weakCtx.get()?.dataStore?.data ?: return 0L
    return data
      .map { preferences ->
        preferences[apiTimestampKey] ?: 0L
      }.first()
  }

  suspend fun setLastFetchedApiTimestamp(timestamp: Long) {
    val dataStore = weakCtx.get()?.dataStore ?: return
    dataStore.edit { preferences ->
      preferences[apiTimestampKey] = timestamp
    }
  }
}