package com.adyen.android.assignment.repository

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.exceptions.FetchVenueException
import com.adyen.android.assignment.model.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

class MainRepository(
  private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider,
  private val placesService: PlacesService = PlacesService.instance,
) {
  private var lastUserLocation: Position? = null

  suspend fun getUserLocation(): Position {
    return withContext(dispatcherProvider.io) {
      delay(500)
      val location = Position(52.379189, 4.899431)
      lastUserLocation = location
      location
    }
  }

  suspend fun getVenues(): Result<List<Unit>> = try {
    withContext(dispatcherProvider.io) {
      val location = lastUserLocation ?: getUserLocation()
      val query = VenueRecommendationsQueryBuilder()
        .setLatitudeLongitude(location.lat, location.lng)
        .build()

      val response = placesService.getVenueRecommendations(query).awaitResponse()
      if (response.isSuccessful) {
        val results = response.body()?.results ?: emptyList()
        println(">> Fetched venues: $results")
        Result.success(emptyList())
      } else {
        Result.failure(FetchVenueException())
      }
    }
  } catch (e: Exception) {
    e.printStackTrace()
    Result.failure(FetchVenueException())
  }
}