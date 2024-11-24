package com.amd.ahmadmsa.feature_places.presentation.locationScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable.LoaderCompose
import com.amd.ahmadmsa.feature_places.presentation.util.StateResource

@Composable
fun LocationScreen(
    locationState: StateResource<Pair<Double, Double>>,
    modifier: Modifier = Modifier,
    onLocationReceived: () -> Unit
) {

    val context = LocalContext.current


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
            onLocationReceived()

        }

        is StateResource.Error -> {
            LaunchedEffect(locationState) {
                Toast.makeText(
                    context,
                    locationState.errorMessage ?: context.getString(locationState.errorString),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

}