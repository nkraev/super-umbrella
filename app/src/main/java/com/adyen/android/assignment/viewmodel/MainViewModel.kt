package com.adyen.android.assignment.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.mappers.PositionToLatLngMapper
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.repository.MainRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class VenuesState {
  data object Initial : VenuesState()
  data class Loaded(val venues: List<Venue>) : VenuesState()
}

sealed class MainViewState {
  data object Loading : MainViewState()

  @Immutable
  data class Ready(
    val position: LatLng,
    val venues: VenuesState = VenuesState.Initial,
  ) : MainViewState()
}

class MainViewModel(
  private val repository: MainRepository = MainRepository(),
  private val mapper: PositionToLatLngMapper = PositionToLatLngMapper,
) : ViewModel() {
  private val _state = MutableStateFlow<MainViewState>(MainViewState.Loading)
  val state: StateFlow<MainViewState> = _state

  init {
    // hardcoding Ams location for now
    viewModelScope.launch {
      val location = repository.getUserLocation().let(mapper::map)
      _state.value = MainViewState.Ready(location)

      val venues = repository.getVenues().getOrElse { emptyList() }
      _state.value = MainViewState.Ready(location, VenuesState.Loaded(venues))
    }
  }

  fun onTopUpClicked() {
    viewModelScope.launch {
      repository.getVenues()
    }
  }
}