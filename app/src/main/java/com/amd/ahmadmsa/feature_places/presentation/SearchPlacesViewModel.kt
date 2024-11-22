package com.amd.ahmadmsa.feature_places.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amd.ahmadmsa.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.usecase.SearchCommonPlacesUseCase
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import com.amd.ahmadmsa.feature_places.presentation.model.PlaceLists
import com.amd.ahmadmsa.feature_places.presentation.util.PresentationConstants
import com.amd.ahmadmsa.feature_places.presentation.util.StateResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchPlacesViewModel @Inject constructor(
    private val searchCommonPlacesUseCase: SearchCommonPlacesUseCase
) : ViewModel() {


    var placeSearchState by mutableStateOf<StateResource<PlaceLists>>(StateResource.Loading(false))
        private set

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _snackBarEvent = MutableSharedFlow<Int>()
    val snackBarEvent: SharedFlow<Int> = _snackBarEvent

    init {
        observeUserLocation()
    }


    val onActionTriggered: (Pair<Double, Double>) -> Unit = { location ->
        updateUserLocation(location)
    }

    private fun updateUserLocation(location: Pair<Double, Double>) {
        _userLocation.value = location
    }


    private fun observeUserLocation() {
        viewModelScope.launch {
            userLocation.collect { location ->
                location?.let {
                    fetchNearByPlaces()
                }
            }
        }
    }

    private fun fetchNearByPlaces() {
        viewModelScope.launch {
            placeSearchState = StateResource.Loading(true)
            val location = userLocation.value
            if (location == null) {
                placeSearchState = StateResource.Error(errorString = R.string.user_location_unavailable)
                return@launch
            }
            val (latitude, longitude) = location
            val juiceQuery = FsqNearbyQueryParam(ll = "$latitude,$longitude", query = "juice", radius = 5000, limit = 50)
            val pizzaQuery = FsqNearbyQueryParam(ll = "$latitude,$longitude", query = "pizza", radius = 5000, limit = 50)

            placeSearchState = when (val resource = searchCommonPlacesUseCase(juiceQuery, pizzaQuery)) {
                is Resource.Loading -> {
                    StateResource.Loading(true, R.string.fetching_nearby_places)
                }

                is Resource.Success -> {
                    val filteredResponse = resource.data
                    if (filteredResponse != null) {
                        val placeLists = PlaceLists(
                            juicePlaces = filteredResponse.placesFromQuery1.results,
                            pizzaPlaces = filteredResponse.placesFromQuery2.results,
                            commonPlaces = filteredResponse.commonPlaces
                        )
                        if (placeLists.commonPlaces.isEmpty()) {
                            _snackBarEvent.emit(R.string.no_common_place_found)
                        }
                        StateResource.Success(placeLists)
                    } else {
                        StateResource.Error(errorString = R.string.no_data_found_for_places)
                    }
                }

                is Resource.Error -> {
                    if (resource.code == PresentationConstants.ErrorCode.NETWORK_FAILURE) {
                        StateResource.Error(R.string.internet_connection)
                    } else {
                        val errorMessage = resource.message
                        StateResource.Error(R.string.something_went_wrong, errorMessage)
                    }
                }
            }
        }
    }

}