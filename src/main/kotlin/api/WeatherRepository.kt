package api

import api.dto.request.Geocoding
import api.dto.response.WeatherResponse

interface WeatherRepository {

    fun getLocationListByName(name: String): List<Geocoding>

    fun getWeatherByCoordinates(latitude: Double, longitude: Double): WeatherResponse
}