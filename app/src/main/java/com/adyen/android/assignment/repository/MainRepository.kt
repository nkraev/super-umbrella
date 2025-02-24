package com.adyen.android.assignment.repository

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.exceptions.FetchVenueException
import com.adyen.android.assignment.db.VenueDao
import com.adyen.android.assignment.di.ServiceLocator
import com.adyen.android.assignment.mappers.ResultToVenueCategoryEntityMapper
import com.adyen.android.assignment.mappers.ResultToVenueEntityMapper
import com.adyen.android.assignment.mappers.VenueEntityToVenueMapper
import com.adyen.android.assignment.model.Position
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.prefs.AppPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

// extracting for testability
interface DispatcherProvider {
  val io: CoroutineDispatcher
  val main: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
  override val io: CoroutineDispatcher
    get() = Dispatchers.IO
  override val main: CoroutineDispatcher
    get() = Dispatchers.Main
}

data class VenueLoadResult(
  val venues: List<Venue>,
  val source: String,
)

class MainRepository(
  private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider,
  private val placesService: PlacesService = PlacesService.instance,
  private val entityToVenueMapper: VenueEntityToVenueMapper = VenueEntityToVenueMapper(),
  private val resultToVenueEntityMapper: ResultToVenueEntityMapper = ResultToVenueEntityMapper(),
  private val resultToVenueCategoryEntityMapper: ResultToVenueCategoryEntityMapper = ResultToVenueCategoryEntityMapper(),
  private val appPreferences: AppPreferences = AppPreferences,
  private val systemTimeProvider: () -> Long = ServiceLocator.currentTimeMillis,
) {
  // ideally needs to be constructor injection as well, but it's too complicated to setup DI for the assignment
  private val venueDao: VenueDao by lazy { ServiceLocator.venueDao }
  private val locationUpdates = MutableSharedFlow<Position>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  private var lastUserLocation: Position? = null

  fun listenToLocationUpdates(): Flow<Position> = locationUpdates

  fun updateLocation(position: Position) {
    lastUserLocation = position
    val result = locationUpdates.tryEmit(position)
    println(">> Send position successfully: $position, result: $result")
  }

  suspend fun getVenues(): Result<VenueLoadResult> = try {
    val lastFetchedTimestamp = appPreferences.getLastFetchedApiTimestamp()
    val currentTime = systemTimeProvider()
    if (currentTime - lastFetchedTimestamp > VENUE_FETCH_INTERVAL) {
      fetchVenuesFromApiAndSave()
      appPreferences.setLastFetchedApiTimestamp(currentTime)
      Result.success(
        VenueLoadResult(
          venues = fetchVenuesFromDb(),
          source = "API",
        )
      )
    } else {
      val venues = fetchVenuesFromDb()
      Result.success(
        VenueLoadResult(
          venues = venues,
          source = "Database",
        )
      )
    }
  } catch (e: Exception) {
    e.printStackTrace()
    if (e is FetchVenueException) {
      appPreferences.setLastFetchedApiTimestamp(0)
      Result.failure(e)
    } else Result.failure(FetchVenueException())
  }

  suspend fun saveUserAllowedLocationPrefs(isAllowed: Boolean = true) {
    withContext(dispatcherProvider.io) {
      appPreferences.setLocationAllowed(isAllowed)
    }
  }

  fun listenToLocationAllowedChanges() = appPreferences.isLocationAllowed()

  fun listenToPermissionRequestStatusChanges() = appPreferences.getPermissionRequestStatus()

  suspend fun savePermissionRequestStatus(status: String) {
    withContext(dispatcherProvider.io) {
      appPreferences.setPermissionRequestStatus(status)
    }
  }

  private suspend fun fetchVenuesFromApiAndSave() {
    return withContext(dispatcherProvider.io) {
      val location = lastUserLocation ?: listenToLocationUpdates().first()

      val query = VenueRecommendationsQueryBuilder()
        .setLatitudeLongitude(location.lat, location.lng)
        .build()

      println(">> Fetching venues with query: $query")

      val response = placesService.getVenueRecommendations(query).awaitResponse()
      if (response.isSuccessful) {
        val results = response.body()?.results ?: emptyList()
        println(">> Fetched venues: $results")

        val categories = results.map(resultToVenueCategoryEntityMapper::map)
        val venues = results.map(resultToVenueEntityMapper::map)
        println(">> Saving categories: $categories")
        venueDao.saveAllCategories(categories)

        println(">> Saving venues: $venues")
        venueDao.saveAllVenues(venues)
      } else {
        throw FetchVenueException()
      }
    }
  }

  private suspend fun fetchVenuesFromDb(): List<Venue> {
    println(">> Fetching venues from db")
    return withContext(dispatcherProvider.io) {
      venueDao.getAll().map(entityToVenueMapper::map)
    }
  }

  companion object {
    const val VENUE_FETCH_INTERVAL = 5 * 60 * 1000L // 5 minutes
  }
}