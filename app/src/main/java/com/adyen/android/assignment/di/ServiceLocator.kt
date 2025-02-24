package com.adyen.android.assignment.di

import android.content.Context
import androidx.room.Room
import com.adyen.android.assignment.db.VenueDatabase
import com.adyen.android.assignment.repository.MainRepository

object ServiceLocator {
  private lateinit var database: VenueDatabase

  val venueDao by lazy { database.venueDao() }

  val currentTimeMillis: () -> Long
    get() = { System.currentTimeMillis() }

  fun init(applicationContext: Context) {
    database = Room
      .databaseBuilder(applicationContext, VenueDatabase::class.java, "venue-db")
      .build()

    println(">> ServiceLocator initialized")
  }

  val mainRepository by lazy {
    MainRepository(
      systemTimeProvider = currentTimeMillis
    )
  }
}