package com.amd.ahmadmsa.feature_places.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.amd.ahmadmsa.feature_places.data.model.FourSquareNearbyResponseDTO
import com.amd.ahmadmsa.feature_places.data.model.FsqNearbyQueryParamDto
import com.amd.ahmadmsa.feature_places.data.model.LocationLlDto
import com.amd.ahmadmsa.feature_places.data.model.NearByLocationsForBothDto
import com.amd.ahmadmsa.feature_places.data.model.toDomain
import com.amd.ahmadmsa.feature_places.data.model.toDto
import com.amd.ahmadmsa.feature_places.data.service.FourSquareAPI
import com.amd.ahmadmsa.feature_places.data.util.DataConstants
import com.amd.ahmadmsa.feature_places.data.util.DataResource
import com.amd.ahmadmsa.feature_places.data.util.NetworkStateChecker
import com.amd.ahmadmsa.feature_places.data.util.mapToDomain
import com.amd.ahmadmsa.feature_places.data.util.toDomain
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.amd.ahmadmsa.feature_places.domain.model.NearByLocationsForBoth
import com.amd.ahmadmsa.feature_places.domain.repository.SearchPlacesRepository
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Singleton
@SuppressLint("MissingPermission")
class SearchPlacesRepositoryImpl @Inject constructor(
    private val networkStateChecker: NetworkStateChecker,
    private val fourSquareAPI: FourSquareAPI,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : SearchPlacesRepository {


    override suspend fun getUserLocation(): Flow<Resource<LocationLl>> {
        return if (networkStateChecker.hasInternetConnection()) {
            getUserLocationInternal()
                .mapToDomain { locationDto -> locationDto.toDomain() }
        } else {
            flowOf(Resource.Error(null, errorCode = DataConstants.ErrorCode.NETWORK_FAILURE))
        }
    }


    private suspend fun getUserLocationInternal(): Flow<DataResource<LocationLlDto>> = flow {
        emit(DataResource.Loading())
        try {
            val locationResult = getLastKnownLocation()
            locationResult.let {
                val locationDto = it?.let { it1 -> LocationLlDto(it1.latitude, it.longitude) }
                locationDto?.let { emit(DataResource.Success(locationDto, code = DataConstants.ErrorCode.SUCCESS)) }
            } ?: run {
                emit(DataResource.Error(null, errorCode = DataConstants.ErrorCode.CONTENT_NOT_AVAILABLE))
            }
        } catch (e: Exception) {
            emit(
                DataResource.Error(
                    message = " Exception${e.localizedMessage}",
                    errorCode = DataConstants.ErrorCode.EXCEPTION_CODE
                )
            )
        }
    }


    private suspend fun getLastKnownLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }


    override suspend fun getNearByLocationsForBoth(
        query1: FsqNearbyQueryParam,
        query2: FsqNearbyQueryParam
    ): Resource<NearByLocationsForBoth> {

        return if (networkStateChecker.hasInternetConnection()) {
            fetchNearbyLocationsForBoth(
                query1.toDto(),
                query2.toDto()
            ).toDomain { nearByLocationsForBothDto -> nearByLocationsForBothDto.toDomain() }
        } else {
            Resource.Error(null, errorCode = DataConstants.ErrorCode.NETWORK_FAILURE)
        }
    }


    private suspend fun fetchNearbyLocationsForBoth(
        query1: FsqNearbyQueryParamDto,
        query2: FsqNearbyQueryParamDto
    ): DataResource<NearByLocationsForBothDto> {
        return try {
            coroutineScope {
                val response1Deferred = async { fetchNearbyForQuery(query1) }
                val response2Deferred = async { fetchNearbyForQuery(query2) }

                val response1 = response1Deferred.await()
                val response2 = response2Deferred.await()

                if (response1 is DataResource.Error) {
                    DataResource.Error(
                        message = response1.message,
                        errorCode = response1.code
                    )
                } else if (response2 is DataResource.Error) {
                    DataResource.Error(
                        message = response2.message,
                        errorCode = response2.code
                    )
                } else if (response1 is DataResource.Success && response2 is DataResource.Success) {
                    val combinedResult = NearByLocationsForBothDto(
                        placesFromQuery1 = response1.data!!,
                        placesFromQuery2 = response2.data!!
                    )
                    DataResource.Success(combinedResult, DataConstants.ErrorCode.SUCCESS)
                } else {
                    DataResource.Error(null, DataConstants.ErrorCode.UNKNOWN_ERROR)
                }
            }
        } catch (e: Exception) {
            DataResource.Error(null, DataConstants.ErrorCode.EXCEPTION_CODE)
        }
    }


    private suspend fun fetchNearbyForQuery(queryDto: FsqNearbyQueryParamDto): DataResource<FourSquareNearbyResponseDTO> {
        return retry(4) {
            try {
                val response = fourSquareAPI.getNearbyLocations(
                    ll = queryDto.ll,
                    radius = queryDto.radius,
                    query = queryDto.query,
                    limit = queryDto.limit,
                    fields = DataConstants.FOUR_SQUARE_QUERY_FIELDS
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        DataResource.Success(responseBody, code = response.code())
                    } else {
                        DataResource.Error(
                            message = null,
                            errorCode = DataConstants.ErrorCode.RESPONSE_BODY_NULL
                        )
                    }
                } else {
                    DataResource.Error(
                        message = response.message(),
                        errorCode = response.code()
                    )
                }
            } catch (e: Exception) {
                DataResource.Error(
                    message = e.localizedMessage,
                    errorCode = DataConstants.ErrorCode.EXCEPTION_CODE
                )
            }
        }
    }


    private suspend fun <T> retry(
        times: Int,
        block: suspend () -> DataResource<T>
    ): DataResource<T> {
        var lastError: DataResource<T>? = null
        repeat(times) {
            val result = try {
                block()
            } catch (e: Exception) {
                DataResource.Error(
                    message = e.localizedMessage,
                    errorCode = DataConstants.ErrorCode.EXCEPTION_CODE
                )
            }

            if (result is DataResource.Success) {
                return result
            } else if (result is DataResource.Error) {
                lastError = result
            }
        }
        return lastError ?: DataResource.Error(null, errorCode = DataConstants.ErrorCode.RETRY_FINISHED)
    }


}