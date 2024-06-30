package servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import models.dao.SessionDao
import models.dao.UserDao
import models.entities.Session
import models.entities.User
import utils.Constants.EMAIL
import utils.Constants.ERROR
import utils.Constants.PASSWORD
import utils.Constants.PASSWORD_CONFIRMATION
import utils.Constants.SESSION_ID
import utils.Constants.SIGN_UP
import utils.md5
import java.time.LocalDateTime

@WebServlet(name = "SignUpServlet", urlPatterns = ["/sign-up"])
class SignUpServlet(
    private val userDao: UserDao = UserDao(),
    private val sessionDao: SessionDao = SessionDao(),
) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        templateEngine.process(SIGN_UP, context, response.writer)
    }

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val email = request.getParameter(EMAIL)
        val password = request.getParameter(PASSWORD)
        val passwordAgain = request.getParameter(PASSWORD_CONFIRMATION)

        if (email.isNullOrBlank()) {
            context.setVariable(ERROR, "Email cannot be blank")
            templateEngine.process(SIGN_UP, context, response.writer)
        }

        if (password.isNullOrBlank() || passwordAgain.isNullOrBlank()) {
            context.setVariable(ERROR, "Password cannot be blank")
            templateEngine.process(SIGN_UP, context, response.writer)
        }

        if (password != passwordAgain) {
            context.setVariable(ERROR, "Password mismatch")
            context.setVariable(EMAIL, email)
            templateEngine.process(SIGN_UP, context, response.writer)
        }

        val user = userDao
            .insert(User(login = email, password = password.md5()))
            .getOrThrow()

        val session = sessionDao
            .insert(Session(user = user, expiresAt = LocalDateTime.now().withHour(1)))
            .getOrThrow()

        response.addCookie(Cookie(SESSION_ID, session.id.toString()))
        response.sendRedirect(request.contextPath + "/")
    }
}