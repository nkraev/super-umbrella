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

class MainViewModel(
  private val repository: MainRepository = ServiceLocator.mainRepository,
  private val mapper: PositionToLatLngMapper = PositionToLatLngMapper,
) : ViewModel() {
  private val _state =
    MutableStateFlow<MainViewState>(MainViewState.Loading)
  val state: StateFlow<MainViewState> = _state

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
  }

  fun userAllowedToRequestPermissions() {
    viewModelScope.launch {
      repository.saveUserAllowedLocationPrefs(isAllowed = true)
      _state.value = MainViewState.Loading
    }
  }
}