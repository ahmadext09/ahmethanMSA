package com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.ui.theme.APP_RED
import com.amd.ahmadmsa.ui.theme.GREEN
import com.amd.ahmadmsa.ui.theme.White



@Composable
fun PlaceSelectionCompose(
    modifier: Modifier,
    juicePlaces: List<Place>,
    pizzaPlaces: List<Place>
) {
    var showJuicePlaces by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val listState = rememberLazyListState()


    LaunchedEffect(showJuicePlaces) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp, 10.dp)
    ) {
        Text(
            text = when {
                showJuicePlaces == true -> stringResource(R.string.juice_places)
                showJuicePlaces == false -> stringResource(R.string.pizza_places)
                else -> ""
            },
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            textAlign = TextAlign.Center,
            color = APP_RED
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (showJuicePlaces != null) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (showJuicePlaces == true && juicePlaces.isNotEmpty()) {

                        items(juicePlaces) { place ->
                            PlaceItemCompose(
                                placeName = place.name,
                                placeAddress = place.location?.address,
                                placeDistance = place.distance.toString()
                            )
                        }
                    } else if (showJuicePlaces == false && pizzaPlaces.isNotEmpty()) {

                        items(pizzaPlaces) { place ->
                            PlaceItemCompose(
                                placeName = place.name,
                                placeAddress = place.location?.address,
                                placeDistance = place.distance.toString()
                            )
                        }
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {

                    showJuicePlaces = true

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GREEN,
                    contentColor = White
                )
            ) {
                Text(text = stringResource(R.string.juice_places))
            }

            Button(
                onClick = { showJuicePlaces = false },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GREEN,
                    contentColor = White
                )
            ) {
                Text(text = stringResource(R.string.pizza_places))
            }
        }
    }
}

