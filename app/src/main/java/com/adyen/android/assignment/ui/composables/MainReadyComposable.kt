package com.adyen.android.assignment.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.ui.composables.utils.mapVenuesToMarker
import com.adyen.android.assignment.viewmodel.MainViewModel
import com.adyen.android.assignment.viewmodel.MainViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun MainReadyComposable(ready: MainViewState.Ready, viewModel: MainViewModel) {
  val cameraPositionState = rememberCameraPositionState()
  var mapIsReady by remember { mutableStateOf(false) }
  var selectedVenue by remember { mutableStateOf<Venue?>(null) }
  val markers = remember(ready.venues) {
    mapVenuesToMarker(ready.venues) { venue ->
      selectedVenue = venue
    }
  }

  LaunchedEffect(mapIsReady) {
    if (!mapIsReady) return@LaunchedEffect
    cameraPositionState.animate(
      update = CameraUpdateFactory.newLatLngZoom(ready.position, 16f),
      durationMs = 1000,
    )
  }

  if (selectedVenue != null) {
    BottomSheetVenueInformation(venue = selectedVenue!!, onDismiss = { selectedVenue = null })
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = viewModel::onTopUpClicked, // method reference is referentially transparent so we don't need to remember { .. } it
      ) {
        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Top-up your balance")
      }
    },
  ) { padding ->
    GoogleMap(
      mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
      cameraPositionState = cameraPositionState,
      onMapLoaded = { mapIsReady = true },
      uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
      ),
      modifier = Modifier.padding(padding),
    ) {
      markers.map { marker ->
        Marker(
          state = rememberUpdatedMarkerState(position = marker.position),
          onClick = { _ -> marker.onClick(); true },
        )
      }
    }
  }

}