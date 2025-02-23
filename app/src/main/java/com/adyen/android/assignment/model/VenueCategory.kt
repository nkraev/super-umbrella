package com.adyen.android.assignment.model

import androidx.compose.runtime.Immutable

@Immutable
data class VenueCategory(
  val icon: String,
  val id: String,
  val type: String,
)