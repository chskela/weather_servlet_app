package controllers

import api.dto.response.WeatherResponse
import exception.*
import jakarta.servlet.ServletConfig
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import models.entities.Session
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.WebContext
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver
import org.thymeleaf.web.IWebApplication
import org.thymeleaf.web.servlet.JakartaServletWebApplication
import utils.Constants.ERROR
import utils.Constants.INDEX
import utils.Constants.LOGIN
import utils.Constants.SIGN_UP
import utils.Constants.WEATHERS
import java.time.LocalDateTime

abstract class BaseServlet : HttpServlet() {
    private lateinit var application: JakartaServletWebApplication
    lateinit var templateEngine: ITemplateEngine
    lateinit var context: WebContext

    override fun init(config: ServletConfig) {
        application = JakartaServletWebApplication.buildApplication(config.servletContext)
        templateEngine = buildTemplateEngine(application)
        super.init()
    }

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        val webExchange = application.buildExchange(req, resp)
        context = WebContext(webExchange, webExchange.locale)

        try {
            super.service(req, resp)
        } catch (e: RuntimeException) {
            when (e) {
                is BadSessionException,
                is CookieNotFoundException,
                is SessionNotFoundException -> {
                    context.setVariable(WEATHERS, emptyList<WeatherResponse>())
                    context.setVariable(LOGIN, null)
                    templateEngine.process(INDEX, context, resp.writer)
                }

                is UserExistsException -> {
                    context.setVariable(ERROR, e.message)
                    templateEngine.process(SIGN_UP, context, resp.writer)
                }

                is WeatherApiException -> {
                    context.setVariable(
                        ERROR,
                        "The weather service is temporarily unavailable, please try again later."
                    )
                    templateEngine.process(ERROR, context, resp.writer)
                }

                else -> {
                    println("BaseServlet: $e")
                    templateEngine.process(ERROR, context, resp.writer)
                }
            }
        }
    }

    private fun buildTemplateEngine(application: IWebApplication): ITemplateEngine {
        val templateResolver = WebApplicationTemplateResolver(application)
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.prefix = "/WEB-INF/templates/"
        templateResolver.suffix = ".html"
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.cacheTTLMs = 3600000L
        templateResolver.isCacheable = false

        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)
        return templateEngine
    }

    protected fun findCookieBySessionId(cookies: List<Cookie>, sessionId: String): Result<Cookie> {
        val cookie = cookies.find { it.name == sessionId }
        return if (cookie != null) {
            Result.success(cookie)
        } else Result.failure(CookieNotFoundException(sessionId))
    }

    protected fun isBadSession(session: Session): Boolean = session.expiresAt.isBefore(LocalDateTime.now())
}