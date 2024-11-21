package com.amd.ahmadmsa.feature_places.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class LocationLl(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude")val longitude: Double
) : Serializable