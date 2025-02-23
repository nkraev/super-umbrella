package com.adyen.android.assignment.db

import androidx.room.Database

@Database(
  entities = [VenueEntity::class, VenueEntityCategory::class, VenueCategoryEntity::class],
  version = 1
)
abstract class VenueDatabase {
  abstract fun venueDao(): VenueDao
}