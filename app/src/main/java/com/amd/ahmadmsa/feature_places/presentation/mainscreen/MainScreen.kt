package com.amd.ahmadmsa.feature_places.presentation.mainscreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.CommonPlacesCompose
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.LoaderCompose
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.PlaceSelectionCompose
import com.amd.ahmadmsa.feature_places.presentation.model.PlaceLists
import com.amd.ahmadmsa.feature_places.presentation.util.StateResource
import kotlinx.coroutines.flow.SharedFlow


@Composable
fun MainScreen(
    locationState: StateResource<Pair<Double, Double>>,
    placeSearchState: StateResource<PlaceLists>,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    snackBarEvent: SharedFlow<Int>,
    onLocationReceived: (Pair<Double, Double>) -> Unit
) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        snackBarEvent.collect { stringRes ->
            snackBarHostState.showSnackbar(context.getString(stringRes))
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        when (locationState) {
            is StateResource.Loading -> {
                if (locationState.isLoading) {
                    locationState.messageString?.let {
                        LoaderCompose(
                            message = stringResource(id = it)
                        )
                    }
                }
            }

            is StateResource.Success -> {
                onLocationReceived(locationState.data)

                when (placeSearchState) {
                    is StateResource.Loading -> {
                        if (placeSearchState.isLoading) {
                            placeSearchState.messageString?.let {
                                LoaderCompose(
                                    message = stringResource(id = it)
                                )
                            }
                        }
                    }

                    is StateResource.Success -> {
                        val placeLists = placeSearchState.data
                        if (placeLists.commonPlaces.isEmpty()) {
                            PlaceSelectionCompose(
                                modifier = modifier,
                                juicePlaces = placeLists.juicePlaces,
                                pizzaPlaces = placeLists.pizzaPlaces
                            )
                        } else {
                            CommonPlacesCompose(
                                commonPlaces = placeLists.commonPlaces,
                                modifier = modifier
                            )
                        }
                    }

                    is StateResource.Error -> {
                        LaunchedEffect(placeSearchState) {
                            Toast.makeText(
                                context,
                                placeSearchState.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            is StateResource.Error -> {
                LaunchedEffect(locationState) {
                    Toast.makeText(
                        context,
                        locationState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
    }
}
