package com.amd.ahmadmsa.feature_places.domain.usecase

import com.amd.ahmadmsa.feature_places.domain.model.FilteredQueryResponse
import com.amd.ahmadmsa.feature_places.domain.model.FourSquareNearbyResponse
import com.amd.ahmadmsa.feature_places.domain.model.FsqNearbyQueryParam
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.feature_places.domain.repository.SearchPlacesRepository
import com.amd.ahmadmsa.feature_places.domain.util.Resource
import javax.inject.Inject


class SearchCommonPlacesUseCase @Inject constructor(
    private val repository: SearchPlacesRepository
) {
    suspend operator fun invoke(
        query1: FsqNearbyQueryParam,
        query2: FsqNearbyQueryParam
    ): Resource<FilteredQueryResponse> {
        return when (val resource = repository.getNearByLocationsForBoth(query1, query2)) {
            is Resource.Success -> {

                val placeListForQuery1 = getPlaceList(resource.data?.placesFromQuery1)
                val placeListForQuery2 = getPlaceList(resource.data?.placesFromQuery2)
                val commonPlaces = getCommonPlaces(placeListForQuery1, placeListForQuery2)


                Resource.Success(
                    data = buildFilteredQueryResponse(
                        placeListForQuery1,
                        placeListForQuery2,
                        commonPlaces
                    ),
                    code = resource.code
                )
            }

            is Resource.Error -> {

                Resource.Error(
                    message = resource.message,
                    errorCode = resource.code,
                    data = buildFilteredQueryResponse(
                        arrayListOf(),
                        arrayListOf(),
                        arrayListOf()
                    )
                )
            }

            is Resource.Loading -> {

                Resource.Loading(
                    data = buildFilteredQueryResponse(
                        arrayListOf(),
                        arrayListOf(),
                        arrayListOf()
                    )
                )
            }
        }
    }


    private fun getPlaceList(response: FourSquareNearbyResponse?): ArrayList<Place> {
        return response?.results ?: arrayListOf()
    }


    private fun getCommonPlaces(
        placeListForQuery1: ArrayList<Place>,
        placeListForQuery2: ArrayList<Place>
    ): ArrayList<Place> {
        val commonPlaces = ArrayList<Place>()
        if (placeListForQuery1.isNotEmpty() && placeListForQuery2.isNotEmpty()) {
            val query1PlaceIds = placeListForQuery1.map { it.fsqId }.toHashSet()
            commonPlaces.addAll(placeListForQuery2.filter { query1PlaceIds.contains(it.fsqId) })
        }
        return commonPlaces
    }


    private fun buildFilteredQueryResponse(
        placeListForQuery1: ArrayList<Place>,
        placeListForQuery2: ArrayList<Place>,
        commonPlaces: ArrayList<Place>
    ): FilteredQueryResponse {
        return FilteredQueryResponse(
            placesFromQuery1 = FourSquareNearbyResponse(results = placeListForQuery1),
            placesFromQuery2 = FourSquareNearbyResponse(results = placeListForQuery2),
            commonPlaces = commonPlaces
        )
    }
}
