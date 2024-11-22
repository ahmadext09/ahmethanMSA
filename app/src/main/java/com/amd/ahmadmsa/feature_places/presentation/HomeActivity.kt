package com.amd.ahmadmsa.feature_places.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amd.ahmadmsa.feature_places.presentation.mainscreen.MainScreen
import com.amd.ahmadmsa.ui.theme.AhmadMSATheme
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            homeViewModel.onPermissionResult(isGranted)
        }

        setContent {
            AhmadMSATheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { innerPadding ->
                    NavHost(navController, startDestination = "main") {
                        composable("main") {
                            val searchPlacesViewModel: SearchPlacesViewModel = hiltViewModel(it)
                            MainScreen(
                                locationState = homeViewModel.locationState,
                                placeSearchState = searchPlacesViewModel.placeSearchState,
                                modifier = Modifier.padding(innerPadding),
                                snackBarHostState = snackBarHostState,
                                snackBarEvent = searchPlacesViewModel.snackBarEvent,
                                onLocationReceived = { location ->
                                    searchPlacesViewModel.onActionTriggered(location)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (homeViewModel.userLocation.value == null) {
            if (!isPermissionGranted()) {
                requestPermission()
            } else {
                homeViewModel.onPermissionResult(granted = true)
            }
        }

    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

}