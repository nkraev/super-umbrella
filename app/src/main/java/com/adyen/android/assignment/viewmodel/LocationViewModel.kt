package com.adyen.android.assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.di.ServiceLocator
import com.adyen.android.assignment.model.PermissionRequestStatus
import com.adyen.android.assignment.repository.MainRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

sealed class LocationEvents {
  data object RequestPermission : LocationEvents()
}

class LocationViewModel(
  private val repository: MainRepository = ServiceLocator.mainRepository,
) : ViewModel() {
  private val _events = MutableSharedFlow<LocationEvents>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )

  val events: SharedFlow<LocationEvents> = _events

  init {
    viewModelScope.launch {
      repository
        .listenToLocationAllowedChanges()
        .filter { it }
        .collectLatest {
          _events.emit(LocationEvents.RequestPermission)
        }
    }
  }

  fun onLocationPermissionResolved(permissionGranted: Boolean) {
    viewModelScope.launch {
      repository.savePermissionRequestStatus(
        if (permissionGranted) PermissionRequestStatus.GRANTED.value
        else PermissionRequestStatus.DENIED.value
      )
    }
  }

  fun onShowLocationPermissionRationale() {
    viewModelScope.launch {
      repository.saveUserAllowedLocationPrefs(isAllowed = false)
      repository.savePermissionRequestStatus(PermissionRequestStatus.SHOW_RATIONALE.value)
    }
  }
}