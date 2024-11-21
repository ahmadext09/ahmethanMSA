package com.amd.ahmadmsa.feature_places.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class FsqNearbyQueryParam(
    @SerializedName("ll")
    val ll: String,

    @SerializedName("radius")
    val radius: Int,

    @SerializedName("query")
    val query: String,

    @SerializedName("limit")
    val limit: Int,

)