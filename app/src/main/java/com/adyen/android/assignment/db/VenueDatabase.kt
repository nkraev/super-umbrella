package com.adyen.android.assignment.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [VenueEntity::class, VenueCategoryEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class VenueDatabase : RoomDatabase() {
  abstract fun venueDao(): VenueDao
}