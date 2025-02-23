package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.model.Position
import com.google.android.gms.maps.model.LatLng

object PositionToLatLngMapper {
  fun map(position: Position): LatLng {
    return LatLng(position.lat, position.lng)
  }
}