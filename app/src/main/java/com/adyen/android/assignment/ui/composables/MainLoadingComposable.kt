package com.adyen.android.assignment.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainLoadingComposable() {
  Scaffold { padding ->
    Box(
      modifier = Modifier
          .padding(padding)
          .fillMaxSize()
    ) {
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
      )
    }
  }
}