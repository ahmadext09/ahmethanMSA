package com.amd.ahmadmsa.feature_places.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.feature_places.domain.usecase.SearchCommonPlacesUseCase
import com.amd.ahmadmsa.feature_places.domain.usecase.UseCaseFindUserLocation
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import com.amd.ahmadmsa.feature_places.presentation.util.PresentationConstants
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

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Idle)
    val permissionState: StateFlow<PermissionState>
        get() = _permissionState


    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState


    private val _juicePlaces = MutableStateFlow<ArrayList<Place>>(arrayListOf())
    val juicePlaces: StateFlow<ArrayList<Place>> = _juicePlaces


    private val _pizzaPlaces = MutableStateFlow<ArrayList<Place>>(arrayListOf())
    val pizzaPlaces: StateFlow<ArrayList<Place>> = _pizzaPlaces


    private val _commonPlaces = MutableStateFlow<ArrayList<Place>>(arrayListOf())
    val commonPlaces: StateFlow<ArrayList<Place>> = _commonPlaces


    private val _snackBarEvent = MutableSharedFlow<Int>()
    val snackBarEvent: SharedFlow<Int> = _snackBarEvent

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    init {
        observeUserLocation()
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            _permissionState.value = PermissionState.Granted
            fetchUserLocation()
        } else {
            _permissionState.value = PermissionState.Denied
        }
    }


    private fun fetchNearByPlaces() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading(R.string.fetching_nearby_places)
            val location = _userLocation.value
            if (location == null) {
                _uiState.value = UiState.Error(R.string.user_location_unavailable)
                return@launch
            }
            val (latitude, longitude) = location

            val juiceQuery = FsqNearbyQueryParam(
                ll = "$latitude,$longitude",
                query = "juice",
                radius = 5000,
                limit = 50
            )
            val pizzaQuery = FsqNearbyQueryParam(
                ll = "$latitude,$longitude",
                query = "pizza",
                radius = 5000,
                limit = 50
            )


            when (val resource = searchCommonPlacesUseCase(juiceQuery, pizzaQuery)) {
                is Resource.Loading -> {
                    _uiState.value = UiState.Loading(R.string.fetching_nearby_places)
                }

                is Resource.Success -> {
                    val filteredResponse = resource.data
                    if (filteredResponse != null) {


                        _juicePlaces.value = filteredResponse.placesFromQuery1.results
                        _pizzaPlaces.value = filteredResponse.placesFromQuery2.results
                        _commonPlaces.value = filteredResponse.commonPlaces


                        if (_commonPlaces.value.isEmpty()) {
                            _uiState.value = UiState.EmptyCommon(
                                juicePlaces = _juicePlaces.value,
                                pizzaPlaces = _pizzaPlaces.value
                            )
                            _snackBarEvent.emit(R.string.no_common_place_found)

                        } else {
                            _uiState.value = UiState.HasCommon(commonPlaces = _commonPlaces.value)
                        }
                    } else {
                        _uiState.value = UiState.Error(R.string.no_data_found_for_places)
                    }
                }

                is Resource.Error -> {
                    if (resource.code == PresentationConstants.ErrorCode.NETWORK_FAILURE) {
                        val errorMessage = null
                        _uiState.value = UiState.Error(R.string.internet_connection, errorMessage = errorMessage)
                    } else {
                        val errorMessage = "${resource.message}"
                        _uiState.value = UiState.Error(R.string.something_went_wrong, errorMessage = errorMessage)
                    }

                    _juicePlaces.value = arrayListOf()
                    _pizzaPlaces.value = arrayListOf()
                    _commonPlaces.value = arrayListOf()
                }
            }
        }
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

    private fun fetchUserLocation() {
        viewModelScope.launch {
            useCaseFindUserLocation().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = UiState.Loading(R.string.fetching_location)
                    }

                    is Resource.Success -> {
                        resource.data?.let {
                            _userLocation.value = it.latitude to it.longitude
                        } ?: run {
                            _uiState.value = UiState.Error(R.string.location_data_empty)
                        }
                    }

                    is Resource.Error -> {
                        if (resource.code == PresentationConstants.ErrorCode.NETWORK_FAILURE) {
                            val errorMessage = null
                            _uiState.value = UiState.Error(R.string.internet_connection, errorMessage = errorMessage)
                        } else {
                            val errorMessage = "${resource.message}"
                            _uiState.value = UiState.Error(R.string.something_went_wrong, errorMessage = errorMessage)
                        }
                    }
                }
            }
        }
    }


}


sealed class UiState {
    data object Idle : UiState()
    data class Loading(val messageRes: Int? = null) : UiState()
    data class EmptyCommon(val juicePlaces: ArrayList<Place>, val pizzaPlaces: ArrayList<Place>) : UiState()
    data class HasCommon(val commonPlaces: ArrayList<Place>) : UiState()
    data class Error(val errorString: Int, val errorMessage: String? = null) : UiState()
}


sealed class PermissionState {
    data object Idle : PermissionState()
    data object Check : PermissionState()
    data object Granted : PermissionState()
    data object Denied : PermissionState()
}



