package api.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Current(
    val condition: Condition,
    @SerialName("temp_c")
    val temp: Double,
    @SerialName("wind_kph")
    val windSpeed: Double,
    @SerialName("wind_dir")
    val windDir: String,
    val humidity: Int,
    @SerialName("pressure_mb")
    val pressure: Double,
    val cloud: Int,
)