package api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class Geocoding(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
)