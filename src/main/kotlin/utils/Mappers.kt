package utils

import api.dto.response.WeatherResponse
import views.dto.WeatherDTO

fun WeatherResponse.toWeatherDTO(): WeatherDTO{
    return WeatherDTO(
        name = location.name,
        country = location.country,
        latitude = location.lat,
        longitude = location.lon,
        temperature = current.temp,
        humidity = current.humidity,
        pressure = current.pressure,
        windSpeed = current.windSpeed,
        windDirection = current.windDir,
        clouds = current.cloud,
        icon = current.condition.icon
    )
}