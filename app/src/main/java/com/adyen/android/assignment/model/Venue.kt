package com.adyen.android.assignment.model

import androidx.compose.runtime.Immutable

@Immutable
data class Venue(
  val category: VenueCategory,
  val distance: Int,
  val location: String,
  val name: String,
  val timezone: String,
)