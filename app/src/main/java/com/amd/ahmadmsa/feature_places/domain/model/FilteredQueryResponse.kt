package com.amd.ahmadmsa.feature_places.domain.model

import androidx.annotation.Keep


@Keep
data class FilteredQueryResponse(
    val placesFromQuery1: FourSquareNearbyResponse,
    val placesFromQuery2: FourSquareNearbyResponse,
    val commonPlaces: ArrayList<Place>
)