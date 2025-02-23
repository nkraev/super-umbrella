package com.adyen.android.assignment.ui.composables.utils

import com.adyen.android.assignment.mappers.PositionToLatLngMapper
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.ui.models.VenueMarker

fun mapVenueToMarker(venue: Venue): VenueMarker {
  return VenueMarker(
    position = PositionToLatLngMapper.map(venue.position),
    onClick = {
    }
  )
}