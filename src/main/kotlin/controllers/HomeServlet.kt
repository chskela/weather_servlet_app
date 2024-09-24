package controllers

import api.WeatherRepository
import api.repository.WeatherRepositoryImpl
import exception.BadSessionException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import models.dao.LocationDao
import models.dao.SessionDao
import models.entities.Location
import services.toWeatherDTO
import utils.Constants.INDEX
import utils.Constants.LOGIN
import utils.Constants.SESSION_ID
import utils.Constants.WEATHERS
import views.dto.WeatherDTO
import java.util.*

@WebServlet(name = "HomeServlet", urlPatterns = [""])
class HomeServlet(
    private val locationDao: LocationDao = LocationDao(),
    private val sessionDao: SessionDao = SessionDao(),
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(),
) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookieBySessionId(request.cookies?.toList() ?: emptyList(), SESSION_ID).getOrThrow()
        val uuid = UUID.fromString(cookie.value)
        val session = sessionDao.findSessionById(uuid).getOrThrow()

        if (isBadSession(session)) {
            throw BadSessionException(session.toString())
        }
        val user = session.user

        val weatherList: List<WeatherDTO> = locationDao.getAllLocationsByUserId(user)
            .map { locations ->
                runBlocking(Dispatchers.IO) {
                    locations.map { location ->
                        async {
                            weatherRepository.getWeatherByCoordinates(location.latitude, location.longitude)
                        }
                    }.awaitAll()
                        .map { weatherResponse -> weatherResponse.toWeatherDTO() }
                }
            }.getOrThrow()

        context.setVariable(WEATHERS, weatherList)
        context.setVariable(LOGIN, user.login)

        templateEngine.process(INDEX, context, response.writer)
    }

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookieBySessionId(request.cookies?.toList() ?: emptyList(), SESSION_ID).getOrThrow()
        val uuid = UUID.fromString(cookie.value)
        val session = sessionDao.findSessionById(uuid).getOrThrow()

        if (isBadSession(session)) {
            throw BadSessionException(session.toString())
        }

        val user = session.user

        val name = request.getParameter("name")
        val latitude = request.getParameter("lat").toDouble()
        val longitude = request.getParameter("lon").toDouble()
        val remove = request.getParameter("remove")

        if (remove != null) {
            locationDao.removeLocationByCoordinateForUser(
                Location(
                    name = name,
                    user = user,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        } else {
            locationDao.insert(
                Location(
                    name = name,
                    user = user,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }

        response.sendRedirect("/")
    }
}