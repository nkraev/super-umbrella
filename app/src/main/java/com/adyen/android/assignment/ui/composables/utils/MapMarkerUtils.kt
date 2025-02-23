package com.adyen.android.assignment.ui.composables.utils

import com.adyen.android.assignment.mappers.PositionToLatLngMapper
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.ui.models.VenueMarker
import com.adyen.android.assignment.viewmodel.VenuesState

fun mapVenuesToMarker(venues: VenuesState, onClick: (Venue) -> Unit): List<VenueMarker> {
  if (venues !is VenuesState.Loaded) return emptyList()
  if (venues.venues.isEmpty()) return emptyList()

  return venues.venues.map { venue ->
    VenueMarker(
      position = PositionToLatLngMapper.map(venue.position),
      onClick = { onClick(venue) }
    )
  }
}