package com.amd.ahmadmsa.di

import android.content.Context
import com.amd.ahmadmsa.feature_places.data.repository.SearchPlacesRepositoryImpl
import com.amd.ahmadmsa.feature_places.data.service.FourSquareAPI
import com.amd.ahmadmsa.feature_places.data.util.NetworkStateChecker
import com.amd.ahmadmsa.feature_places.domain.repository.SearchPlacesRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        networkStateChecker: NetworkStateChecker,
        fourSquareAPI: FourSquareAPI,
        fusedLocationProviderClient: FusedLocationProviderClient
    ): SearchPlacesRepository {
        return SearchPlacesRepositoryImpl(networkStateChecker, fourSquareAPI, fusedLocationProviderClient)
    }
}
