package com.adyen.android.assignment.ui.models

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng

@Immutable
data class VenueMarker(
  val position: LatLng,
  val onClick: () -> Unit,
)