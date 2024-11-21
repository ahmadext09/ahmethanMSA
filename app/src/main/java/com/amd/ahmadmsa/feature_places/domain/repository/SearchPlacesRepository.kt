package com.amd.ahmadmsa.feature_places.domain.repository


import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.amd.ahmadmsa.feature_places.domain.model.NearByLocationsForBoth
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchPlacesRepository {

    suspend fun getUserLocation(): Flow<Resource<LocationLl>>

    suspend fun getNearByLocationsForBoth(
        query1: FsqNearbyQueryParam,
        query2: FsqNearbyQueryParam
    ): Resource<NearByLocationsForBoth>
}