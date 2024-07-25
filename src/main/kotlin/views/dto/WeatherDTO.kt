package views.dto

data class WeatherDTO(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val humidity: Int,
    val pressure: Double,
    val windSpeed: Double,
    val windDirection: String,
    val clouds: Int,
    val icon: String
)