package api.repository

import api.WeatherRepository
import api.config.Config
import api.dto.request.Geocoding
import api.dto.response.WeatherResponse
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class WeatherRepositoryImpl(apiKey: String = Config.weatherApiKey) : WeatherRepository {
    private val baseUri = "http://api.weatherapi.com/v1"
    private val key = "key=$apiKey"
    private val json = Json { ignoreUnknownKeys = true }

    override fun getLocationListByName(name: String): List<Geocoding> {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$baseUri/search.json?q=$name&$key"))
            .GET()
            .build()
        val response = HttpClient.newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val result = json.decodeFromString<List<Geocoding>>(response.join().body())
        return result
    }

    override fun getWeatherByCoordinates(latitude: Double, longitude: Double): WeatherResponse {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$baseUri/current.json?q=$latitude,$longitude&$key"))
            .GET()
            .build()
        val response = HttpClient.newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val result = json.decodeFromString<WeatherResponse>(response.join().body())
        return result
    }
}