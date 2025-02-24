package com.adyen.android.assignment.ui.composables.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adyen.android.assignment.viewmodel.MainViewModel
import com.adyen.android.assignment.viewmodel.PermissionStatus

@Composable
fun PermissionRequiredComposable(
  permissionStatus: PermissionStatus,
  viewModel: MainViewModel,
) {
  Scaffold { padding ->
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
    ) {
      if (permissionStatus == PermissionStatus.Initial) {
        Text(
          text = "We need to request location permissions to show you various spots around your location. Please allow the location in the next dialog.",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
        )

        Button(
          onClick = viewModel::userAllowedToRequestPermissions,
          modifier = Modifier.padding(top = 16.dp)
        ) {
          Text("Allow location permissions")
        }
      }

      if (permissionStatus == PermissionStatus.RequestOnceMore) {
        Text(
          text = "We REALLY need that permission for the app to work. Do you mind trying again and granting the location permission?",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
        )

        Button(
          onClick = viewModel::userAllowedToRequestPermissions,
          modifier = Modifier.padding(top = 16.dp)
        ) {
          Text("Allow location permissions")
        }
      }

      if (permissionStatus == PermissionStatus.Denied) {
        Text(
          text = "Location permission denied. Please allow the location permission in settings to continue.",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}