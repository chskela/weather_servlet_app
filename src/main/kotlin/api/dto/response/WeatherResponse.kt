package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current: Current,
    val locationResponse: LocationResponse
)