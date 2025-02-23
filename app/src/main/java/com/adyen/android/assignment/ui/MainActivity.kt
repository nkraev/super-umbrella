package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adyen.android.assignment.ui.composables.MainLoadingComposable
import com.adyen.android.assignment.ui.composables.MainReadyComposable
import com.adyen.android.assignment.ui.theme.AppTheme
import com.adyen.android.assignment.viewmodel.MainViewModel
import com.adyen.android.assignment.viewmodel.MainViewState

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      val viewModel by viewModels<MainViewModel>()
      val state by viewModel.state.collectAsStateWithLifecycle()

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
fun MainActivityComposable(state: MainViewState, viewModel: MainViewModel) {
  when (state) {
    is MainViewState.Ready -> MainReadyComposable(state, viewModel)
    is MainViewState.Loading -> MainLoadingComposable()
  }
}
