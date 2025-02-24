package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adyen.android.assignment.ui.composables.effects.LocationHandlerEffect
import com.adyen.android.assignment.ui.composables.main.MainLoadingComposable
import com.adyen.android.assignment.ui.composables.main.MainReadyComposable
import com.adyen.android.assignment.ui.composables.main.PermissionRequiredComposable
import com.adyen.android.assignment.ui.theme.AppTheme
import com.adyen.android.assignment.viewmodel.LocationViewModel
import com.adyen.android.assignment.viewmodel.MainViewModel
import com.adyen.android.assignment.viewmodel.MainViewState
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    // ideally we need to check if Google Play Services are available
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    setContent {
      val viewModel by viewModels<MainViewModel>()
      val locationViewModel by viewModels<LocationViewModel>()
      val state by viewModel.state.collectAsStateWithLifecycle()

      LocationHandlerEffect(
        viewModel = locationViewModel,
        fusedLocationProviderClient = fusedLocationProviderClient,
        activity = this,
      )

      AppTheme {
        MainActivityComposable(
          state = state,
          viewModel = viewModel,
        )
      }
    }
  }
}

@Composable
fun MainActivityComposable(
  state: MainViewState,
  viewModel: MainViewModel,
) {
  when (state) {
    is MainViewState.PermissionRequired -> PermissionRequiredComposable(
      permissionStatus = state.permissionStatus,
      viewModel = viewModel
    )

    is MainViewState.Ready -> MainReadyComposable(state, viewModel)
    is MainViewState.Loading -> MainLoadingComposable()
  }
}
