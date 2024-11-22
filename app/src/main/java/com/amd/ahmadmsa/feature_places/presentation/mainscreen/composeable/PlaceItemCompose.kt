package com.amd.ahmadmsa.feature_places.presentation.mainscreen.composeable


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amd.ahmadmsa.R
import com.amd.ahmadmsa.ui.theme.APP_RED


@Composable
fun PlaceItemCompose(
    placeName: String?,
    placeAddress: String?,
    placeDistance: String?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
        ) {

            Image(
                painter = painterResource(id = R.drawable.wave_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.place_name, placeName ?: stringResource(R.string.not_available)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = APP_RED
                )
                Text(
                    text = stringResource(
                        R.string.place_address,
                        placeAddress ?: stringResource(R.string.not_available)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = APP_RED
                )
                Text(
                    text = if (placeDistance != null) {
                        stringResource(R.string.place_distance, placeDistance)
                    } else {
                        stringResource(R.string.not_available)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = APP_RED
                )
            }
        }
    }
}

