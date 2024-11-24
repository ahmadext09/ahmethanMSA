package com.amd.ahmadmsa.feature_places.presentation.mainscreen

import android.widget.Toast
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
    placeSearchState: StateResource<PlaceLists>,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    snackBarEvent: SharedFlow<Int>,
) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        snackBarEvent.collect { stringRes ->
            snackBarHostState.showSnackbar(context.getString(stringRes))
        }
    }


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
                    placeSearchState.errorMessage ?: context.getString(placeSearchState.errorString),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
