package com.amd.ahmadmsa.feature_places.presentation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.FilteredQueryResponse
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.amd.ahmadmsa.feature_places.domain.usecase.SearchCommonPlacesUseCase
import com.amd.ahmadmsa.feature_places.domain.usecase.UseCaseFindUserLocation
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
class HomeViewModel @Inject constructor(
    private val useCaseFindUserLocation: UseCaseFindUserLocation,
    private val searchCommonPlacesUseCase: SearchCommonPlacesUseCase
) : ViewModel() {

    var locationState by mutableStateOf<StateResource<Pair<Double, Double>>>(StateResource.Loading(false))
        private set

    var placeSearchState by mutableStateOf<StateResource<PlaceLists>>(StateResource.Loading(false))
        private set

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _snackBarEvent = MutableSharedFlow<Int>()
    val snackBarEvent: SharedFlow<Int> = _snackBarEvent

    init {
        observeUserLocation()
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


    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            fetchUserLocation()
        } else {
            locationState = StateResource.Error(R.string.location_permission_denied)
        }
    }


    private fun fetchUserLocation() {
        viewModelScope.launch {
            useCaseFindUserLocation()
                .collect { resource -> updateStateFromResource(resource) }
        }
    }


    private fun updateStateFromResource(resource: Resource<LocationLl>) {
        locationState = when (resource) {
            is Resource.Loading -> StateResource.Loading(isLoading = true, R.string.fetching_location)

            is Resource.Success -> {
                val location = resource.data?.let { it.latitude to it.longitude }
                if (location != null) {
                    _userLocation.value = location
                    StateResource.Success(location)
                } else {
                    StateResource.Error(R.string.location_data_empty)
                }
            }

            is Resource.Error -> {
                if (resource.code == PresentationConstants.ErrorCode.NETWORK_FAILURE) {
                    StateResource.Error(errorString = R.string.internet_connection)
                } else {
                    val errorMessage = resource.message
                    StateResource.Error(errorString = R.string.something_went_wrong, errorMessage = errorMessage)
                }
            }
        }
    }


    private fun fetchNearByPlaces() {
        viewModelScope.launch {
            placeSearchState = StateResource.Loading(true, R.string.fetching_nearby_places)
            val location = userLocation.value
            if (location == null) {
                placeSearchState = StateResource.Error(errorString = R.string.user_location_unavailable)
                return@launch
            }
            val resource = makeNearbyPlacesApiCall(location)
            handleNearbyPlacesResponse(resource)
        }
    }


    private suspend fun makeNearbyPlacesApiCall(location: Pair<Double, Double>): Resource<FilteredQueryResponse> {
        val (latitude, longitude) = location
        val juiceQuery = FsqNearbyQueryParam(ll = "$latitude,$longitude", query = "juice", radius = 5000, limit = 50)
        val pizzaQuery = FsqNearbyQueryParam(ll = "$latitude,$longitude", query = "pizza", radius = 5000, limit = 50)
        return searchCommonPlacesUseCase(juiceQuery, pizzaQuery)
    }


    private suspend fun handleNearbyPlacesResponse(resource: Resource<FilteredQueryResponse>) {
        placeSearchState = when (resource) {
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