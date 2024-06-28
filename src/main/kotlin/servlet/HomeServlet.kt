package servlet

import api.WeatherRepository
import api.dto.WeatherData
import api.repository.WeatherRepositoryImpl
import exception.BadSessionException
import exception.SessionNotFoundException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import models.dao.LocationDao
import models.dao.SessionDao
import models.dao.UserDao
import java.util.*

@WebServlet(name = "HomeServlet", urlPatterns = [""])
class HomeServlet(
    private val userDao: UserDao = UserDao(),
    private val locationDao: LocationDao = LocationDao(),
    private val sessionDao: SessionDao = SessionDao(),
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(),
) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookieBySessionId(request.cookies.toList(), "sessionId").getOrThrow()
        val uuid = UUID.fromString(cookie.value)
        val session = sessionDao.findSessionById(uuid).getOrElse { throw SessionNotFoundException(uuid.toString()) }

        if (isBadSession(session)) {
            throw BadSessionException(session.toString())
        }
        val user = session.user

        val locations = locationDao.getAllLocationsByUserId(user.id ?: 0).getOrElse {
            emptyList()
        }

        val weatherList: List<WeatherData> = runBlocking(Dispatchers.IO) {
            locations.map { location ->
                async {
                    weatherRepository.getWeatherByCoordinates(location.latitude, location.longitude)
                }
            }.awaitAll()
        }

        context.setVariable("weathers", weatherList)
        context.setVariable("login", user.login)

        templateEngine.process("index", context, response.writer)
    }
}