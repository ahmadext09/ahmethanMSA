package com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.ui.theme.APP_RED

@Composable
fun CommonPlacesCompose(commonPlaces: List<Place>, modifier:Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp, 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.common_place),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            color = APP_RED
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(commonPlaces) { place ->
                PlaceItemCompose( placeName = place.name, placeAddress = place.location?.address, placeDistance = place.distance.toString())
            }
        }
    }
}
