package services

import models.dao.SessionDao
import java.time.LocalDateTime
import java.util.*

class SessionService {

    fun removeSessionExpiredAtTime(date: Date) {
        val timer = Timer()
        val task = Task()
        val period = 1000L * 60 * 60 * 24

        timer.schedule(task, date, period)
    }

    private class Task(private val sessionDao: SessionDao = SessionDao()) : TimerTask() {
        override fun run() {
            sessionDao.removeSessionExpiredAtTime(LocalDateTime.now())
        }
    }
}