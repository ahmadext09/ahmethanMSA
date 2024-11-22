package com.amd.ahmadmsa.feature_places.presentation.util


sealed class StateResource<T> {
    data class Loading<T>(val isLoading: Boolean = false, val messageString: Int? = null) : StateResource<T>()
    data class Success<T>(val data: T) : StateResource<T>()
    data class Error<T>(val errorString: Int, val errorMessage: String? = null) : StateResource<T>()
}