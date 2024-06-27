package api

import api.dto.WeatherData

interface WeatherRepository {

    fun getWeatherByCity(city: String): WeatherData

    fun getWeatherByCoordinates(latitude: Double, longitude: Double): WeatherData
}