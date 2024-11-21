package com.amd.ahmadmsa.feature_places.data.model

import androidx.annotation.Keep
import com.amd.ahmadmsa.feature_places.data.util.DataConstants
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.google.gson.annotations.SerializedName


@Keep
data class FsqNearbyQueryParamDto(
    @SerializedName("ll")
    val ll: String,

    @SerializedName("radius")
    val radius: Int,

    @SerializedName("query")
    val query: String,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("fields")
    val fields: String = DataConstants.FOUR_SQUARE_QUERY_FIELDS

)



fun FsqNearbyQueryParam.toDto(): FsqNearbyQueryParamDto {
    return FsqNearbyQueryParamDto(
        ll = this.ll,
        radius = this.radius,
        query = this.query,
        limit = this.limit
    )
}