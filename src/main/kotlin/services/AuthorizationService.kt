package services

import exception.PasswordWrongException
import models.dao.SessionDao
import models.dao.UserDao
import models.entities.Session
import models.entities.User
import views.dto.AuthorizationDTO
import utils.PasswordUtils
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

        if (!PasswordUtils.verifyPassword(authorizationDto.password, user.password)) {
            return Result.failure(PasswordWrongException())
        }
        return sessionDao
            .insert(Session(user = user, expiresAt = localDateTimePlusDay()))
    }

    fun signUp(authorizationDto: AuthorizationDTO): Result<Session> {
        return userDao
            .insert(authorizationDto.copy(password = PasswordUtils.hashPassword(authorizationDto.password)).toUser())
            .map { user ->
                sessionDao
                    .insert(Session(user = user, expiresAt = localDateTimePlusDay()))
                    .getOrThrow()
            }
    }

    fun logout(uuid: UUID) {
        sessionDao.removeSessionById(uuid)
    }

    private fun localDateTimePlusDay(): LocalDateTime = LocalDateTime.now().plusDays(1)
}

fun AuthorizationDTO.toUser() = User(login = email, password = password)