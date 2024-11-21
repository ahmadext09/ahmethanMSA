package com.amd.ahmadmsa.feature_places.presentation.place_listing_scr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.feature_places.presentation.HomeViewModel
import com.amd.ahmadmsa.feature_places.presentation.PlaceItemCompose

@Composable
fun ListScreen(type: String, homeViewModel: HomeViewModel) {
    val list: ArrayList<Place> = when (type) {
        "juice" -> homeViewModel.juicePlaces.collectAsState().value
        "pizza" -> homeViewModel.pizzaPlaces.collectAsState().value
        else -> arrayListOf()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(list) { place ->

            PlaceItemCompose(
                placeName = place.name,
                placeAddress = place.location?.address,
                placeDistance = place.distance.toString()
            )
        }
    }
}
