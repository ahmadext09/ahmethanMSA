package com.amd.ahmadmsa.feature_places.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class NearByLocationsForBoth(
    @SerializedName("placesFromQuery1")
    val placesFromQuery1: FourSquareNearbyResponse,

    @SerializedName("placesFromQuery2")
    val placesFromQuery2: FourSquareNearbyResponse
) : Serializable


