package com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.ui.theme.GREEN
import com.amd.ahmadmsa.ui.theme.White

@Composable
fun PlaceSelectionCompose(
    juicePlaces: ArrayList<Place>,
    pizzaPlaces: ArrayList<Place>,
    modifier: Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =  Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("list/juice") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GREEN,
                contentColor = White,
                )
        ) {
            Text(text = stringResource(R.string.juice_places))
        }

        Button(
            onClick = { navController.navigate("list/pizza") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = GREEN,
                contentColor = White,
            )
        ) {
            Text(text = stringResource(R.string.pizza_places))
        }
    }
}
