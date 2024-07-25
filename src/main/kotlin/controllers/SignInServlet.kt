package controllers

import exception.UserNotExistsException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import models.dao.SessionDao
import models.dao.UserDao
import models.entities.Session
import utils.Constants.EMAIL
import utils.Constants.EMAIL_CANNOT_BE_BLANK
import utils.Constants.ERROR
import utils.Constants.PASSWORD
import utils.Constants.PASSWORD_CANNOT_BE_BLANK
import utils.Constants.PASSWORD_DOES_NOT_MATCH
import utils.Constants.SESSION_ID
import utils.Constants.SIGN_IN
import utils.Constants.USER_DOES_NOT_EXIST
import utils.md5
import java.time.LocalDateTime

@WebServlet(name = "SignInServlet", urlPatterns = ["/sign-in"])
class SignInServlet(
    private val userDao: UserDao = UserDao(),
    private val sessionDao: SessionDao = SessionDao(),
) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        templateEngine.process(SIGN_IN, context, response.writer)
    }

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val email = request.getParameter(EMAIL)
        val password = request.getParameter(PASSWORD)

        if (email.isNullOrBlank()) {
            context.setVariable(ERROR, EMAIL_CANNOT_BE_BLANK)
            templateEngine.process(SIGN_IN, context, response.writer)
        } else if (password.isNullOrBlank()) {
            context.setVariable(ERROR, PASSWORD_CANNOT_BE_BLANK)
            context.setVariable(EMAIL, email)
            templateEngine.process(SIGN_IN, context, response.writer)
        } else {
            val user = userDao.findUserByLogin(email)
                .onFailure { e ->
                    if (e is UserNotExistsException) {
                        context.setVariable(ERROR, USER_DOES_NOT_EXIST)
                        context.setVariable(EMAIL, email)
                        templateEngine.process(SIGN_IN, context, response.writer)
                    } else throw e
                }.getOrThrow()

            if (password.md5() != user.password) {
                context.setVariable(ERROR, PASSWORD_DOES_NOT_MATCH)
                context.setVariable(EMAIL, email)
                templateEngine.process(SIGN_IN, context, response.writer)
            } else {
                val session = sessionDao
                    .insert(Session(user = user, expiresAt = LocalDateTime.now().withHour(1)))
                    .getOrThrow()

                response.addCookie(Cookie(SESSION_ID, session.id.toString()))
                response.sendRedirect(request.contextPath + "/")
            }
        }
    }
}