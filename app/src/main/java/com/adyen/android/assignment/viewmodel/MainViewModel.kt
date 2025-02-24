package com.adyen.android.assignment.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.di.ServiceLocator
import com.adyen.android.assignment.mappers.PositionToLatLngMapper
import com.adyen.android.assignment.model.PermissionRequestStatus
import com.adyen.android.assignment.model.Venue
import com.adyen.android.assignment.repository.MainRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class VenuesState {
  data object Initial : VenuesState()
  data class Loaded(val venues: List<Venue>) : VenuesState()
}

sealed class PermissionStatus {
  data object Initial : PermissionStatus()
  data object RequestOnceMore : PermissionStatus()
  data object Denied : PermissionStatus()
}

sealed class MainViewState {
  data class PermissionRequired(val permissionStatus: PermissionStatus) : MainViewState()
  data object Loading : MainViewState()

  @Immutable
  data class Ready(
    val position: LatLng,
    val venues: VenuesState = VenuesState.Initial,
  ) : MainViewState()
}

sealed class MainViewEvents {
  data class Message(val message: String) : MainViewEvents()
  data class LoadingError(val message: String) : MainViewEvents()
}

class MainViewModel(
  private val repository: MainRepository = ServiceLocator.mainRepository,
  private val mapper: PositionToLatLngMapper = PositionToLatLngMapper,
) : ViewModel() {
  private val _state =
    MutableStateFlow<MainViewState>(MainViewState.Loading)
  val state: StateFlow<MainViewState> = _state

  private val _events = MutableSharedFlow<MainViewEvents>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  val events: Flow<MainViewEvents> = _events

  init {
    viewModelScope.launch {
      repository
        .listenToPermissionRequestStatusChanges()
        .map(PermissionRequestStatus::fromValue)
        .collectLatest {
          when (it) {
            PermissionRequestStatus.INITIAL -> _state.value = MainViewState.PermissionRequired(
              PermissionStatus.Initial
            )

            PermissionRequestStatus.SHOW_RATIONALE -> _state.value =
              MainViewState.PermissionRequired(PermissionStatus.RequestOnceMore)

            PermissionRequestStatus.DENIED -> _state.value =
              MainViewState.PermissionRequired(PermissionStatus.Denied)

            else -> {}
          }
        }
    }

    viewModelScope.launch {
      repository
        .listenToLocationUpdates()
        .collectLatest {
          val position = mapper.map(it)
          println(">> Position is set in viewmodel: $position")
          _state.value = MainViewState.Ready(position)

          repository.getVenues().fold(
            onSuccess = { result ->
              _state.value = MainViewState.Ready(position, VenuesState.Loaded(result.venues))
              _events.tryEmit(MainViewEvents.Message("Loaded ${result.venues.size} venues from ${result.source}"))
            },
            onFailure = {
              _events.tryEmit(MainViewEvents.LoadingError("Failed to load venues"))
            }
          )
        }
    }
  }

  fun userAllowedToRequestPermissions() {
    viewModelScope.launch {
      repository.saveUserAllowedLocationPrefs(isAllowed = true)
      _state.value = MainViewState.Loading
    }
  }
}