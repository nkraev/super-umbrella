package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.api.model.Location
import com.adyen.android.assignment.api.model.Main
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.db.VenueEntity

class ResultToVenueEntityMapper() {
  fun map(result: Result): VenueEntity {
    return VenueEntity(
      distance = result.distance,
      location = result.location.mapLocation(),
      name = result.name,
      timezone = result.timezone,
      categoryId = result.categories.firstOrNull()?.id ?: "",
      registryBalance = 0L,
      position = result.geocodes?.main?.mapPosition() ?: "0.0;0.0",
    )
  }

  private fun Location.mapLocation(): String {
    return "$address, $locality, $postcode, $country"
  }

  private fun Main.mapPosition(): String {
    return "$latitude;$longitude"
  }
}