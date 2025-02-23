package com.adyen.android.assignment.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.adyen.android.assignment.model.Position
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.model.VenueCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetVenueInformation(venue: Venue, onDismiss: () -> Unit) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
  ) {
    BottomSheetContents(venue)
  }
}

@Composable
fun ColumnScope.BottomSheetContents(venue: Venue) {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      AsyncImage(
        model = venue.category.icon,
        contentDescription = venue.category.type,
        modifier = Modifier
          .size(32.dp)
          .background(Color.Gray),
      )
      Text(
        text = venue.name,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(start = 16.dp)
      )
    }
    Text(text = venue.location, style = MaterialTheme.typography.bodyMedium)
    Text(
      text = "Distance: ${venue.distance}m from you",
      style = MaterialTheme.typography.bodyMedium
    )
    Text(text = "Timezone: ${venue.timezone}", style = MaterialTheme.typography.bodyMedium)
    Button(
      onClick = { /* do nothing */ },
    ) {
      Text(text = "Buy from them")
    }
  }
}

@Preview
@Composable
fun BottomSheetVenueInformationPreview() {
  Column(
    modifier = Modifier
      .width(300.dp)
      .background(Color.White)
  ) {
    BottomSheetContents(
      Venue(
        name = "Venue Name",
        location = "Venue Location",
        category = VenueCategory(
          id = "13701",
          icon = "https://ss3.4sqi.net/img/categories_v2/travel/trainstation_bg_88.png",
          type = "Train station",
        ),
        distance = 79,
        timezone = "Europe/Amsterdam",
        position = Position(52.379189, 4.899431),
      )
    )
  }
}