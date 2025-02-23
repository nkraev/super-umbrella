package com.adyen.android.assignment.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.adyen.android.assignment.viewmodel.MainViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MainReadyComposable(ready: MainViewState.Ready) {
    val cameraPositionState = rememberCameraPositionState()
    var mapIsReady by remember { mutableStateOf(false) }

    LaunchedEffect(mapIsReady) {
        if (!mapIsReady) return@LaunchedEffect
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(ready.position, 12f),
            durationMs = 2000,
        )
    }

    GoogleMap(
        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
        cameraPositionState = cameraPositionState,
        onMapLoaded = { mapIsReady = true },
    )
}