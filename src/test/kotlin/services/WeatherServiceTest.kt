package services

import api.WeatherRepository
import api.dto.request.Geocoding
import api.dto.response.Condition
import api.dto.response.Current
import api.dto.response.LocationResponse
import api.dto.response.WeatherResponse
import models.entities.Location
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import views.dto.WeatherDTO
import kotlin.test.assertEquals

class WeatherServiceTest {

    private val weatherRepository: WeatherRepository = mock()
    private val weatherService = WeatherService(weatherRepository)

    @Test
    fun `getWeatherByCoordinates should return list of WeatherDTO`() {
        val coordinates = listOf(Location(latitude = 1.0, longitude = 1.0), Location(latitude = 2.0, longitude = 2.0))
        val weatherResponse1 = WeatherResponse(
            locationResponse = LocationResponse(
                name = "City1",
                country = "Country1",
                lat = 1.0,
                lon = 1.0
            ),
            current = Current(
                temp = 10.0,
                humidity = 50,
                pressure = 1000.0,
                windSpeed = 5.0,
                windDir = "N",
                cloud = 50,
                condition = Condition(icon = "icon1", text = "icon1")
            )
        )
        val weatherResponse2 = WeatherResponse(
            locationResponse = LocationResponse(
                name = "City2",
                country = "Country2",
                lat = 2.0,
                lon = 2.0
            ),
            current = Current(
                temp = 20.0,
                humidity = 60,
                pressure = 1010.0,
                windSpeed = 6.0,
                windDir = "S",
                cloud = 60,
                condition = Condition(icon = "icon2", text = "icon2")
            )
        )
        val weatherDTO1 = WeatherDTO(
            name = "City1",
            country = "Country1",
            latitude = 1.0,
            longitude = 1.0,
            temperature = 10.0,
            humidity = 50,
            pressure = 1000.0,
            windSpeed = 5.0,
            windDirection = "N",
            clouds = 50,
            icon = "icon1"
        )
        val weatherDTO2 = WeatherDTO(
            name = "City2",
            country = "Country2",
            latitude = 2.0,
            longitude = 2.0,
            temperature = 20.0,
            humidity = 60,
            pressure = 1010.0,
            windSpeed = 6.0,
            windDirection = "S",
            clouds = 60,
            icon = "icon2"
        )

        whenever(weatherRepository.getWeatherByCoordinates(any(), any())).thenReturn(weatherResponse1, weatherResponse2)

        val result = weatherService.getWeatherByCoordinates(coordinates)

        assertEquals(setOf(weatherDTO1, weatherDTO2), result.toSet())
    }

    @Test
    fun `getLocationListByName should return list of Geocoding`() {
        val name = "City"
        val geocoding1 = Geocoding(name = "City1", lat = 1.0, lon = 1.0, country = "Country1")
        val geocoding2 = Geocoding(name = "City2", lat = 2.0, lon = 2.0, country = "Country2")

        whenever(weatherRepository.getLocationListByName(any())).thenReturn(listOf(geocoding1, geocoding2))

        val result = weatherService.getLocationListByName(name)

        assertEquals(listOf(geocoding1, geocoding2), result)
    }
}
