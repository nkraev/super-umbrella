package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.db.VenueCategoryEntity
import com.adyen.android.assignment.db.VenueEntityCategory
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.model.VenueCategory

class VenueEntityToVenueMapper {
  fun map(entity: VenueEntityCategory): Venue {
    return Venue(
      category = entity.category.map(),
      distance = entity.venue.distance,
      location = entity.venue.location,
      name = entity.venue.name,
      timezone = entity.venue.timezone,
    )
  }

  private fun VenueCategoryEntity.map(): VenueCategory {
    return VenueCategory(
      id = id,
      type = type,
      icon = icon,
    )
  }
}