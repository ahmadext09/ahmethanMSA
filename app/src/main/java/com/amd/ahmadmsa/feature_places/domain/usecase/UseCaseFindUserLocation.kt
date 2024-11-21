package com.amd.ahmadmsa.feature_places.domain.usecase

import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.amd.ahmadmsa.feature_places.domain.repository.SearchPlacesRepository
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UseCaseFindUserLocation @Inject constructor(
    private val repository: SearchPlacesRepository
) {
    suspend operator fun invoke(): Flow<Resource<LocationLl>> = repository.getUserLocation()
}