package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.Icon
import com.adyen.android.assignment.api.model.Result
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultToVenueCategoryEntityMapperTest {
  private val mapper = ResultToVenueCategoryEntityMapper()

  @Test
  fun `map Result to VenueCategoryEntity correctly`() {
    // Arrange
    val result = Result(
      categories = listOf(
        Category(
          icon = Icon(
            prefix = "https://ss3.4sqi.net/img/categories_v2/travel/trainstation_",
            suffix = ".png"
          ),
          id = "19047",
          name = "Rail Station"
        )
      ),
      distance = 78,
      geocodes = null,
      location = com.adyen.android.assignment.api.model.Location(
        address = "Stationsplein 15",
        country = "NL",
        locality = "Amsterdam",
        neighbourhood = null,
        postcode = "1012 AB",
        region = "Noord-Holland"
      ),
      name = "Amsterdam Central Railway Station (Station Amsterdam Centraal)",
      timezone = "Europe/Amsterdam"
    )

    val expectedId = "19047"
    val expectedIcon = "https://ss3.4sqi.net/img/categories_v2/travel/trainstation_bg_88.png"
    val expectedType = "Rail Station"

    // Act
    val venueCategoryEntity = mapper.map(result)

    // Assert
    assertEquals(expectedId, venueCategoryEntity.id)
    assertEquals(expectedIcon, venueCategoryEntity.icon)
    assertEquals(expectedType, venueCategoryEntity.type)
  }

  @Test
  fun `map Result with no categories to VenueCategoryEntity correctly`() {
    // Arrange
    val result = Result(
      categories = emptyList(),
      distance = 150,
      geocodes = null,
      location = com.adyen.android.assignment.api.model.Location(
        address = "Teststraat 1",
        country = "BE",
        locality = "Brussels",
        neighbourhood = null,
        postcode = "1000",
        region = "Brussels-Capital Region"
      ),
      name = "Test Venue",
      timezone = "Europe/Brussels"
    )

    val expectedId = ""
    val expectedIcon = ""
    val expectedType = ""

    // Act
    val venueCategoryEntity = mapper.map(result)

    // Assert
    assertEquals(expectedId, venueCategoryEntity.id)
    assertEquals(expectedIcon, venueCategoryEntity.icon)
    assertEquals(expectedType, venueCategoryEntity.type)
  }
}