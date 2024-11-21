package com.amd.ahmadmsa.feature_places.data.model

import androidx.annotation.Keep
import com.amd.ahmadmsa.feature_places.domain.model.NearByLocationsForBoth
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class NearByLocationsForBothDto(
    @SerializedName("placesFromQuery1")
    val placesFromQuery1: FourSquareNearbyResponseDTO,

    @SerializedName("placesFromQuery2")
    val placesFromQuery2: FourSquareNearbyResponseDTO
) : Serializable




fun NearByLocationsForBothDto.toDomain(): NearByLocationsForBoth {
    return NearByLocationsForBoth(
        placesFromQuery1 = placesFromQuery1.toDomain(),
        placesFromQuery2 = placesFromQuery2.toDomain()
    )
}