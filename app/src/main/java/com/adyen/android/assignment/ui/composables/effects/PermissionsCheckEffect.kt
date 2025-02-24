package com.adyen.android.assignment.ui.composables.effects

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adyen.android.assignment.viewmodel.LocationEvents
import com.adyen.android.assignment.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PermissionsCheckEffect(
  viewModel: LocationViewModel,
  activity: Activity,
) {

  fun onResult(permissionGranted: Boolean) {
    when {
      permissionGranted -> viewModel.onLocationPermissionResolved(true)
      ActivityCompat.shouldShowRequestPermissionRationale(
        activity, Manifest.permission.ACCESS_FINE_LOCATION
      ) -> viewModel.onShowLocationPermissionRationale()

      else -> viewModel.onLocationPermissionResolved(false)
    }
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = ::onResult,
  )

  val ctx = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(coroutineScope) {
    viewModel.events.collectLatest {
      when (it) {
        is LocationEvents.RequestPermission -> {
          if (locationPermissionGranted(ctx)) {
            viewModel.onLocationPermissionResolved(true)
          } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
          }
        }
      }
    }
  }
}

private fun locationPermissionGranted(context: Context) =
  ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
  ) == PackageManager.PERMISSION_GRANTED