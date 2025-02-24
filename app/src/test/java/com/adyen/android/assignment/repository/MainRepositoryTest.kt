package com.adyen.android.assignment.repository

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.exceptions.FetchVenueException
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.GeoCode
import com.adyen.android.assignment.api.model.Icon
import com.adyen.android.assignment.api.model.Location
import com.adyen.android.assignment.api.model.Main
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.db.VenueCategoryEntity
import com.adyen.android.assignment.db.VenueDao
import com.adyen.android.assignment.db.VenueEntity
import com.adyen.android.assignment.db.VenueEntityCategory
import com.adyen.android.assignment.di.ServiceLocator
import com.adyen.android.assignment.mappers.ResultToVenueCategoryEntityMapper
import com.adyen.android.assignment.mappers.ResultToVenueEntityMapper
import com.adyen.android.assignment.mappers.VenueEntityToVenueMapper
import com.adyen.android.assignment.model.Position
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.model.VenueCategory
import com.adyen.android.assignment.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response as RetrofitResponse

@ExperimentalCoroutinesApi
class MainRepositoryTest {

  private lateinit var repository: MainRepository
  private lateinit var mockPlacesService: PlacesService
  private lateinit var mockVenueDao: VenueDao
  private lateinit var mockAppPreferences: AppPreferences
  private lateinit var mockEntityToVenueMapper: VenueEntityToVenueMapper
  private lateinit var mockResultToVenueEntityMapper: ResultToVenueEntityMapper
  private lateinit var mockResultToVenueCategoryEntityMapper: ResultToVenueCategoryEntityMapper
  private lateinit var testDispatcherProvider: DispatcherProvider
  private lateinit var systemTimeProvider: () -> Long

  private val testDispatcher = UnconfinedTestDispatcher()

  @Before
  fun setUp() {
    mockPlacesService = mockk(relaxed = true)
    mockVenueDao = mockk(relaxed = true)
    mockAppPreferences = mockk(relaxed = true)
    mockEntityToVenueMapper = mockk(relaxed = true)
    mockResultToVenueEntityMapper = mockk(relaxed = true)
    mockResultToVenueCategoryEntityMapper = mockk(relaxed = true)
    testDispatcherProvider = object : DispatcherProvider {
      override val io = testDispatcher
      override val main = testDispatcher
    }
    systemTimeProvider = mockk()

    // Mock ServiceLocator.venueDao
    mockkObject(ServiceLocator)
    every { ServiceLocator.venueDao } returns mockVenueDao
    every { ServiceLocator.currentTimeMillis } returns systemTimeProvider

    repository = MainRepository(
      dispatcherProvider = testDispatcherProvider,
      placesService = mockPlacesService,
      entityToVenueMapper = mockEntityToVenueMapper,
      resultToVenueEntityMapper = mockResultToVenueEntityMapper,
      resultToVenueCategoryEntityMapper = mockResultToVenueCategoryEntityMapper,
      appPreferences = mockAppPreferences,
      systemTimeProvider = systemTimeProvider
    )

    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `getVenues fetches from API and saves when interval exceeded`() = runTest {
    // Arrange
    val currentTime = 15 * 60 * 1000L // 15 minutes
    val lastFetchedTimestamp = 0L
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
      geocodes = GeoCode(
        main = Main(
          latitude = 52.379189,
          longitude = 4.899431
        )
      ),
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
    val response = ResponseWrapper(results = listOf(result))
    val venueEntity = VenueEntity(
      distance = 100,
      location = "",
      name = "",
      timezone = "",
      categoryId = "",
      registryBalance = 0,
      position="52.379189;4.899431"
    )
    val venueCategoryEntity = VenueCategoryEntity(id = "", icon = "", type = "")
    val venue = Venue(
      name = "",
      location = "",
      distance = 100,
      timezone = "",
      category = VenueCategory(id = "", icon = "", type = ""),
      position = Position(52.379189, 4.899431)
    )
    val expectedResult = VenueLoadResult(
      venues = listOf(venue),
      source = "API"
    )

    val mockCall = mockk<Call<ResponseWrapper>>()
    every { mockCall.execute() } returns RetrofitResponse.success(response)
    every { mockCall.enqueue(any()) } answers {
      firstArg<retrofit2.Callback<ResponseWrapper>>().onResponse(
        mockCall,
        RetrofitResponse.success(response)
      )
    }

    every { systemTimeProvider() } returns currentTime
    coEvery { mockAppPreferences.getLastFetchedApiTimestamp() } returns lastFetchedTimestamp
    coEvery { mockPlacesService.getVenueRecommendations(any()) } returns mockCall
    every { mockResultToVenueEntityMapper.map(result) } returns venueEntity
    every { mockResultToVenueCategoryEntityMapper.map(result) } returns venueCategoryEntity
    every { mockVenueDao.getAll() } returns listOf(venueEntity).map {
      VenueEntityCategory(
        it,
        venueCategoryEntity
      )
    }
    every { mockEntityToVenueMapper.map(any()) } returns venue

    // Act
    repository.updateLocation(Position(52.379189, 4.899431))
    val venuesResult = repository.getVenues()

    // Assert
    coVerify { mockPlacesService.getVenueRecommendations(any()) }
    coVerify { mockVenueDao.saveAllCategories(listOf(venueCategoryEntity)) }
    coVerify { mockVenueDao.saveAllVenues(listOf(venueEntity)) }
    coVerify { mockAppPreferences.setLastFetchedApiTimestamp(currentTime) }
    assertEquals(expectedResult, venuesResult.getOrNull())
  }

  @Test
  fun `getVenues fetches from DB when interval not exceeded`() = runTest {
    // Arrange
    val currentTime = 1000L
    val lastFetchedTimestamp = 900L // within the interval
    val venueEntity = VenueEntity(
      distance = 100,
      location = "",
      name = "",
      timezone = "",
      categoryId = "",
      registryBalance = 0,
      position = "23.33;45.66"
    )
    val venueCategoryEntity = VenueCategoryEntity(id = "", icon = "", type = "")

    val venue = Venue(
      name = "",
      location = "",
      distance = 100,
      timezone = "",
      category = VenueCategory(id = "", icon = "", type = ""),
      position = Position(23.33, 45.66)
    )
    val expectedResult = VenueLoadResult(
      venues = listOf(venue),
      source = "Database"
    )

    every { systemTimeProvider() } returns currentTime
    coEvery { mockAppPreferences.getLastFetchedApiTimestamp() } returns lastFetchedTimestamp
    every { mockVenueDao.getAll() } returns listOf(venueEntity).map {
      VenueEntityCategory(
        it,
        venueCategoryEntity
      )
    }
    every { mockEntityToVenueMapper.map(any()) } returns venue

    // Act
    val venuesResult = repository.getVenues()

    // Assert
    coVerify(exactly = 0) { mockPlacesService.getVenueRecommendations(any()) }
    assertEquals(expectedResult, venuesResult.getOrNull())
  }

  @Test
  fun `getVenues handles API failure`() = runTest {
    // Arrange
    val currentTime = 1000L
    val lastFetchedTimestamp = 0L
    val httpException = HttpException(
      RetrofitResponse.error<Any>(
        404, ResponseBody.create(
          "application/json".toMediaType(),
          "{\"error\":\"Not Found\"}"
        )
      )
    )
    val query = VenueRecommendationsQueryBuilder()
      .setLatitudeLongitude(52.379189, 4.899431)
      .build()

    val mockCall = mockk<Call<ResponseWrapper>>()
    every { mockCall.execute() } throws httpException

    every { systemTimeProvider() } returns currentTime
    coEvery { mockAppPreferences.getLastFetchedApiTimestamp() } returns lastFetchedTimestamp
    coEvery { mockPlacesService.getVenueRecommendations(query) } returns mockCall
    coEvery { mockAppPreferences.setLastFetchedApiTimestamp(0) } returns Unit

    // Act
    val venuesResult = repository.getVenues()

    // Assert
    coVerify(exactly = 0) { mockAppPreferences.setLastFetchedApiTimestamp(0) }
    assertEquals(false, venuesResult.exceptionOrNull() is FetchVenueException)
  }
}