package com.amd.ahmadmsa.feature_places.presentation.mainscreen

import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.CommonPlacesCompose
import com.amd.ahmadmsa.feature_places.presentation.HomeViewModel
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.LoaderCompose
import com.amd.ahmadmsa.feature_places.presentation.PermissionState
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.PlaceSelectionCompose
import com.amd.ahmadmsa.feature_places.presentation.UiState


@Composable
fun MainScreen(
    locationViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    navController: NavController
) {
    val permissionState = locationViewModel.permissionState.collectAsState()
    val uiState = locationViewModel.uiState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        locationViewModel.snackBarEvent.collect { stringRes ->
            snackBarHostState.showSnackbar(context.getString(stringRes))
        }
    }



    when (permissionState.value) {
        is PermissionState.Idle -> {

        }

        is PermissionState.Check -> {
            LoaderCompose(message = stringResource(id = R.string.checking_permission))
        }

        is PermissionState.Granted -> {
            when (uiState.value) {
                is UiState.Idle -> {}
                is UiState.Loading -> {
                    val state = uiState.value as UiState.Loading
                    state.messageRes?.let {
                        LoaderCompose(message = stringResource(id = state.messageRes))
                    }
                }

                is UiState.EmptyCommon -> {
                    val state = uiState.value as UiState.EmptyCommon
                    PlaceSelectionCompose(
                        juicePlaces = state.juicePlaces,
                        pizzaPlaces = state.pizzaPlaces,
                        modifier = modifier,
                        navController = navController,
                    )
                }

                is UiState.HasCommon -> {
                    val state = uiState.value as UiState.HasCommon
                    CommonPlacesCompose(
                        commonPlaces = state.commonPlaces,
                        modifier = modifier
                    )
                }

                is UiState.Error -> {
                    Toast.makeText(
                        LocalContext.current,
                        (uiState.value as UiState.Error).errorMessage
                            ?: stringResource(
                                id = (uiState.value as UiState.Error).errorString
                            ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        is PermissionState.Denied -> {
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.sorry_cant_proceed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

