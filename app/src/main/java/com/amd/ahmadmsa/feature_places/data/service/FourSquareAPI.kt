package com.amd.ahmadmsa.feature_places.data.service

import com.amd.ahmadmsa.BuildConfig
import com.amd.ahmadmsa.feature_places.data.model.FourSquareNearbyResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FourSquareAPI {

    @GET("v3/places/nearby")
    suspend fun getNearbyLocations(
        @Header("Authorization") authorization: String = BuildConfig.FOUR_SQUARE_API_KEY,
        @Query("ll") ll: String,
        @Query("radius") radius: Int,
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ): Response<FourSquareNearbyResponseDTO>


}