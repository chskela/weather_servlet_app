package listeners

import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener
import services.SessionService
import java.util.*

@WebListener
class SessionServletContextListener(private val sessionService: SessionService = SessionService()) :
    ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent?) {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        sessionService.removeSessionExpiredAtTime(date)
    }
}