package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adyen.android.assignment.ui.composables.effects.PermissionsCheckEffect
import com.adyen.android.assignment.ui.composables.main.MainLoadingComposable
import com.adyen.android.assignment.ui.composables.main.MainReadyComposable
import com.adyen.android.assignment.ui.composables.main.PermissionRequiredComposable
import com.adyen.android.assignment.ui.theme.AppTheme
import com.adyen.android.assignment.viewmodel.LocationViewModel
import com.adyen.android.assignment.viewmodel.MainViewModel
import com.adyen.android.assignment.viewmodel.MainViewState

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      val viewModel by viewModels<MainViewModel>()
      val locationViewModel by viewModels<LocationViewModel>()
      val state by viewModel.state.collectAsStateWithLifecycle()

      PermissionsCheckEffect(
        viewModel = locationViewModel,
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
