package services

import exception.PasswordWrongException
import models.dao.SessionDao
import models.dao.UserDao
import models.entities.Session
import services.dto.AuthorizationDTO
import services.dto.toUser
import utils.md5
import java.time.LocalDateTime
import java.util.*

class AuthorizationService(
    private val userDao: UserDao = UserDao(),
    private val sessionDao: SessionDao = SessionDao(),
) {
    fun signIn(authorizationDto: AuthorizationDTO): Result<Session> {
        val user = userDao.findUserByLogin(authorizationDto.email).getOrElse { e ->
            return Result.failure(e)
        }

        if (authorizationDto.password.md5() != user.password) {
            return Result.failure(PasswordWrongException())
        }
        return sessionDao
            .insert(Session(user = user, expiresAt = LocalDateTime.now().withHour(1)))
    }

    fun signUp(authorizationDto: AuthorizationDTO): Result<Session> {
        return userDao
            .insert(authorizationDto.toUser())
            .map { user ->
                sessionDao
                    .insert(Session(user = user, expiresAt = LocalDateTime.now().withHour(1)))
                    .getOrThrow()
            }
    }

    fun logout(uuid: UUID) {
        sessionDao.removeSessionById(uuid)
    }
}