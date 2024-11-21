package com.amd.ahmadmsa.feature_places.data.model

import androidx.annotation.Keep
import com.amd.ahmadmsa.feature_places.domain.model.FourSquareNearbyResponse
import com.amd.ahmadmsa.feature_places.domain.model.Place
import com.amd.ahmadmsa.feature_places.domain.model.Location
import com.google.gson.annotations.SerializedName

@Keep
data class FourSquareNearbyResponseDTO(
    @SerializedName("results")
    val results: ArrayList<PlaceDTO>
)


@Keep
data class PlaceDTO(
    @SerializedName("fsq_id")
    val fsqId: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("distance")
    val distance: Int?,

    @SerializedName("location")
    val location: LocationDTO?

)


@Keep
data class LocationDTO(
    @SerializedName("address")
    val address: String?,

    @SerializedName("address_extended")
    val addressExtended: String?,

    @SerializedName("census_block")
    val censusBlock: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("cross_street")
    val crossStreet: String?,

    @SerializedName("dma")
    val dma: String?,

    @SerializedName("formatted_address")
    val formattedAddress: String?,

    @SerializedName("locality")
    val locality: String?,

    @SerializedName("postcode")
    val postcode: String?,

    @SerializedName("region")
    val region: String?
)

fun FourSquareNearbyResponseDTO.toDomain(): FourSquareNearbyResponse {
    return FourSquareNearbyResponse(
        results = ArrayList(this.results.map { placeDTO ->
            Place(
                fsqId = placeDTO.fsqId,
                name = placeDTO.name,
                distance = placeDTO.distance,
                location = Location(
                    address = placeDTO.location?.address,
                    addressExtended = placeDTO.location?.addressExtended,
                    censusBlock = placeDTO.location?.censusBlock,
                    country = placeDTO.location?.country,
                    crossStreet = placeDTO.location?.crossStreet,
                    dma = placeDTO.location?.dma,
                    formattedAddress = placeDTO.location?.formattedAddress,
                    locality = placeDTO.location?.locality,
                    postcode = placeDTO.location?.postcode,
                    region = placeDTO.location?.region
                )
            )
        })
    )
}
