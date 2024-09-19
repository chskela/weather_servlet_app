package controllers

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import services.AuthorizationService
import services.dto.AuthorizationDTO
import utils.Constants.EMAIL
import utils.Constants.ERROR
import utils.Constants.PASSWORD
import utils.Constants.PASSWORD_CONFIRMATION
import utils.Constants.SESSION_ID
import utils.Constants.SIGN_UP
import utils.PasswordUtils

@WebServlet(name = "SignUpServlet", urlPatterns = ["/sign-up"])
class SignUpServlet(private val authorizationService: AuthorizationService = AuthorizationService()) : BaseServlet() {

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        templateEngine.process(SIGN_UP, context, response.writer)
    }

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val email = request.getParameter(EMAIL)
        val password = request.getParameter(PASSWORD)
        val passwordAgain = request.getParameter(PASSWORD_CONFIRMATION)

        when {
            email.isNullOrBlank() -> {
                context.setVariable(ERROR, "Email cannot be blank")
                templateEngine.process(SIGN_UP, context, response.writer)
            }

            password.isNullOrBlank() || passwordAgain.isNullOrBlank() -> {
                context.setVariable(ERROR, "Password cannot be blank")
                templateEngine.process(SIGN_UP, context, response.writer)
            }

            !PasswordUtils.validatePassword(password) -> {
                context.setVariable(ERROR, """
                    Password length from 8 to 32 characters. 
                    Password complexity: 
                    presence of characters from three categories: uppercase, 
                    lowercase and non-alphabetic characters (numbers).
                    """.trimMargin())
                context.setVariable(EMAIL, email)
                templateEngine.process(SIGN_UP, context, response.writer)
            }

            password != passwordAgain -> {
                context.setVariable(ERROR, "Password mismatch")
                context.setVariable(EMAIL, email)
                templateEngine.process(SIGN_UP, context, response.writer)
            }

            else -> {
                authorizationService.signUp(AuthorizationDTO(email, password))
                    .onSuccess { session ->
                        response.addCookie(Cookie(SESSION_ID, session.id.toString()))
                        response.sendRedirect(request.contextPath + "/")
                    }
            }
        }
    }
}