package com.amd.ahmadmsa.feature_places.presentation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.amd.ahmadmsa.feature_places.domain.usecase.UseCaseFindUserLocation
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import com.amd.ahmadmsa.feature_places.presentation.util.PresentationConstants
import com.amd.ahmadmsa.feature_places.presentation.util.StateResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCaseFindUserLocation: UseCaseFindUserLocation
) : ViewModel() {

    var locationState by mutableStateOf<StateResource<Pair<Double, Double>>>(StateResource.Loading(false))
        private set

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation


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
}