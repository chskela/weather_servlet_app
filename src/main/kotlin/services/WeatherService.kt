package services

import api.WeatherRepository
import api.dto.request.Geocoding
import api.dto.response.WeatherResponse
import api.repository.WeatherRepositoryImpl
import models.entities.Location
import views.dto.WeatherDTO

class WeatherService(private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()) {

    fun getWeatherByCoordinates(coordinates: List<Location>): Result<List<WeatherDTO>> = runCatching {
        coordinates.map { location ->
            weatherRepository.getWeatherByCoordinates(location.latitude, location.longitude)
        }.map { weatherResponse -> weatherResponse.toWeatherDTO() }
    }

    fun getLocationListByName(name: String): Result<List<Geocoding>> = runCatching {
        weatherRepository.getLocationListByName(name)
    }
}

fun WeatherResponse.toWeatherDTO() = WeatherDTO(
    name = locationResponse.name,
    country = locationResponse.country,
    latitude = locationResponse.lat,
    longitude = locationResponse.lon,
    temperature = current.temp,
    humidity = current.humidity,
    pressure = current.pressure,
    windSpeed = current.windSpeed,
    windDirection = current.windDir,
    clouds = current.cloud,
    icon = current.condition.icon
)
