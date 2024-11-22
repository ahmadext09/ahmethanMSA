package com.amd.ahmadmsa.feature_places.presentation.model

import androidx.annotation.Keep
import com.amd.ahmadmsa.feature_places.domain.model.Place

@Keep
data class PlaceLists(
    val juicePlaces: List<Place> = listOf(),
    val pizzaPlaces: List<Place> = listOf(),
    val commonPlaces: List<Place> = listOf()
)