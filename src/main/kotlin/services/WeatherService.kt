package services

import api.WeatherRepository
import api.dto.request.Geocoding
import api.dto.response.WeatherResponse
import api.repository.WeatherRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import models.entities.Location
import views.dto.WeatherDTO

class WeatherService(private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()) {

    fun getWeatherByCoordinates(coordinates: List<Location>):List<WeatherDTO> {
        return runBlocking(Dispatchers.IO) {
            coordinates.map { location ->
                async {
                    weatherRepository.getWeatherByCoordinates(location.latitude, location.longitude)
                }
            }.awaitAll()
                .map { weatherResponse -> weatherResponse.toWeatherDTO() }
        }
    }

    fun getLocationListByName(name: String): List<Geocoding> {
        return weatherRepository.getLocationListByName(name)
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
