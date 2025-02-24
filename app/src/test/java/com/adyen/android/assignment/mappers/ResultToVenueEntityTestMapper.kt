package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.Icon
import com.adyen.android.assignment.api.model.Location
import com.adyen.android.assignment.api.model.Result
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResultToVenueEntityMapperTest {

  private lateinit var mapper: ResultToVenueEntityMapper

  @Before
  fun setUp() {
    mapper = ResultToVenueEntityMapper()
  }

  @Test
  fun `map Result to VenueEntity correctly`() {
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
      location = Location(
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
    val expectedLocation = "Stationsplein 15, Amsterdam, 1012 AB, NL"
    val expectedCategoryId = "19047"
    val expectedRegistryBalance = 0L

    // Act
    val venueEntity = mapper.map(result)

    // Assert
    assertEquals(result.distance, venueEntity.distance)
    assertEquals(expectedLocation, venueEntity.location)
    assertEquals(result.name, venueEntity.name)
    assertEquals(result.timezone, venueEntity.timezone)
    assertEquals(expectedCategoryId, venueEntity.categoryId)
    assertEquals(expectedRegistryBalance, venueEntity.registryBalance)
  }

  @Test
  fun `map Result with no categories to VenueEntity correctly`() {
    // Arrange
    val result = Result(
      categories = emptyList(),
      distance = 150,
      geocodes = null,
      location = Location(
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
    val expectedLocation = "Teststraat 1, Brussels, 1000, BE"
    val expectedCategoryId = ""
    val expectedRegistryBalance = 0L

    // Act
    val venueEntity = mapper.map(result)

    // Assert
    assertEquals(result.distance, venueEntity.distance)
    assertEquals(expectedLocation, venueEntity.location)
    assertEquals(result.name, venueEntity.name)
    assertEquals(result.timezone, venueEntity.timezone)
    assertEquals(expectedCategoryId, venueEntity.categoryId)
    assertEquals(expectedRegistryBalance, venueEntity.registryBalance)
  }
}