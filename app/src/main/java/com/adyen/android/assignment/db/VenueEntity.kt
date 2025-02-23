package com.adyen.android.assignment.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class VenueEntity(
  val distance: Int,
  val location: String,
  @PrimaryKey val name: String,
  val timezone: String,
  val categoryId: String,
  val registryBalance: Long, // in minor monetary unit (cents, pence, etc.)
)

@Entity
data class VenueCategoryEntity(
  val icon: String,
  @PrimaryKey val id: String,
  val type: String,
)

@Entity
data class VenueEntityCategory(
  @Embedded val venue: VenueEntity,
  @Relation(
    parentColumn = "categoryId",
    entityColumn = "id",
  )
  val category: VenueCategoryEntity,
)