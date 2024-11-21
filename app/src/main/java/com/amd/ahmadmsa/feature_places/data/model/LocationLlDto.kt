package com.amd.ahmadmsa.feature_places.data.model

import androidx.annotation.Keep
import com.amd.ahmadmsa.feature_places.domain.model.LocationLl
import com.google.gson.annotations.SerializedName
import java.io.Serializable



@Keep
data class LocationLlDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude")val longitude: Double
) : Serializable

fun LocationLlDto.toDomain(): LocationLl {
    return LocationLl(
        latitude = this.latitude,
        longitude = this.longitude
    )
}