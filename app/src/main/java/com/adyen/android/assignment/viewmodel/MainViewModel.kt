package com.adyen.android.assignment.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class MainViewState {
    data object Loading : MainViewState()
    @Immutable
    data class Ready(
        val position: LatLng,
        val venues: List<Unit> = emptyList(),
    ) : MainViewState()
}

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<MainViewState>(MainViewState.Loading)
    val state: StateFlow<MainViewState> = _state

    init {
        // hardcoding Ams location for now
        _state.value = MainViewState.Ready(
            LatLng(
                52.379189,
                4.899431
            )
        )
    }
}