package controllers

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import services.AuthorizationService
import utils.Constants.SESSION_ID
import java.util.*

@WebServlet(name = "LogoutServlet", urlPatterns = ["/logout"])
class LogoutServlet(private val authorizationService: AuthorizationService = AuthorizationService()) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = findCookieBySessionId(request.cookies.toList(), SESSION_ID).getOrThrow()
        val uuid = UUID.fromString(cookie.value)

        authorizationService.logout(uuid)

        val deletedCookie = Cookie(SESSION_ID, null)
        deletedCookie.maxAge = 0

        response.addCookie(deletedCookie)
        response.sendRedirect(request.contextPath + "/")
    }
}