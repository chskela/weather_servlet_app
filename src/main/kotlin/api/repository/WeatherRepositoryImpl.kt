package api.repository

import api.WeatherRepository
import api.dto.WeatherData
import api.config.Config
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class WeatherRepositoryImpl(private val apiKey: String = Config.weatherApiKey) : WeatherRepository {
    private val baseUri = "https://api.openweathermap.org/data/2.5/weather"

    override fun getWeatherByCity(city: String): WeatherData {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$baseUri?q=$city&appid=$apiKey"))
            .GET()
            .build()
        val response = HttpClient.newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val result = Json.decodeFromString<WeatherData>(response.join().body())
        return result
    }

    override fun getWeatherByCoordinates(latitude: Double, longitude: Double): WeatherData {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$baseUri?lat=$latitude&lon=$longitude&appid=$apiKey"))
            .GET()
            .build()
        val response = HttpClient.newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val result = Json.decodeFromString<WeatherData>(response.join().body())
        return result
    }
}