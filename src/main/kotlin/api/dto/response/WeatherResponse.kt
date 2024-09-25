package api.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current: Current,
    @SerialName("location")
    val locationResponse: LocationResponse
)