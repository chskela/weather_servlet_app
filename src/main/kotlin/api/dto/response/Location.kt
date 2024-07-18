package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
)