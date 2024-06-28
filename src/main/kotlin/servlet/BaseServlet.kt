package servlet

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.WebContext
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver
import org.thymeleaf.web.IWebApplication
import org.thymeleaf.web.servlet.JakartaServletWebApplication

class BaseServlet : HttpServlet() {
    private lateinit var context: WebContext
    private val application = JakartaServletWebApplication.buildApplication(servletContext)
    val templateEngine: ITemplateEngine = buildTemplateEngine(application)

    private fun buildTemplateEngine(application: IWebApplication): ITemplateEngine {
        val templateReolver = WebApplicationTemplateResolver(application)
        templateReolver.templateMode = TemplateMode.HTML
        templateReolver.prefix = "/WEB-INF/templates/"
        templateReolver.suffix = ".html"
        templateReolver.characterEncoding = "UTF-8"
        templateReolver.cacheTTLMs = 3600000L
        templateReolver.isCacheable = true

        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateReolver)
        return templateEngine
    }

    fun setContext(request: HttpServletRequest, response: HttpServletResponse) {
        val webExchange = application.buildExchange(request, response)
        context = WebContext(webExchange, webExchange.locale)
    }
}