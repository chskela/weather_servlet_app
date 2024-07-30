package controllers

import api.WeatherRepository
import api.dto.request.Geocoding
import api.repository.WeatherRepositoryImpl
import exception.BadSessionException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import models.dao.SessionDao
import utils.Constants.LOCATIONS_LIST
import utils.Constants.LOGIN
import utils.Constants.SEARCH_RESULT
import utils.Constants.SESSION_ID
import java.util.*

@WebServlet(name = "SearchServlet", urlPatterns = ["/search"])
class SearchServlet(
    private val sessionDao: SessionDao = SessionDao(),
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()
) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookieBySessionId(request.cookies?.toList() ?: emptyList(), SESSION_ID).getOrThrow()
        val uuid = UUID.fromString(cookie.value)
        val session = sessionDao.findSessionById(uuid).getOrThrow()

        if (isBadSession(session)) {
            throw BadSessionException(session.toString())
        }

        val user = session.user

        val searchRequest = request.getParameter("searchRequest").trim().replace(" ", "+")

        if (searchRequest.isBlank()) {
            response.sendRedirect("/")
        } else {
            val locationsList: List<Geocoding> = weatherRepository.getLocationListByName(searchRequest)

            context.setVariable(LOCATIONS_LIST, locationsList)
            context.setVariable(LOGIN, user.login)

            templateEngine.process(SEARCH_RESULT, context, response.writer)
        }
    }
}