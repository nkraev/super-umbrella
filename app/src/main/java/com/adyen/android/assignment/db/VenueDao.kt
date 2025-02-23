package com.adyen.android.assignment.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface VenueDao {
  @Transaction
  @Query("SELECT * FROM VenueEntity")
  fun getAll(): List<VenueEntityCategory>

  @Insert(entity = VenueEntity::class, onConflict = OnConflictStrategy.REPLACE)
  fun saveAllVenues(venues: List<VenueEntity>)

  @Insert(entity = VenueCategoryEntity::class, onConflict = OnConflictStrategy.REPLACE)
  fun saveAllCategories(categories: List<VenueCategoryEntity>)
}