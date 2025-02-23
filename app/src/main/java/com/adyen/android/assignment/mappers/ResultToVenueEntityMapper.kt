package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.api.model.Location
import com.adyen.android.assignment.api.model.Main
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.db.VenueEntity
import com.adyen.android.assignment.money.MoneyGenerator
import com.adyen.android.assignment.money.RandomMoneyGenerator

class ResultToVenueEntityMapper(
  private val moneyGenerator: MoneyGenerator = RandomMoneyGenerator,
) {
  fun map(result: Result): VenueEntity {
    return VenueEntity(
      distance = result.distance,
      location = result.location.mapLocation(),
      name = result.name,
      timezone = result.timezone,
      categoryId = result.categories.firstOrNull()?.id ?: "",
      registryBalance = moneyGenerator.generateRandomAmount(),
      position = result.geocodes?.main?.mapPosition() ?: "0.0;0.0",
    )
  }

  private fun Location.mapLocation(): String {
    return "$address, $locality, $postcode, $country"
  }

  private fun Main.mapPosition(): String {
    return "$latitude;$longitude"
  }
}