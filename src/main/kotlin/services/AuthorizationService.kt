package services

import exception.PasswordWrongException
import models.dao.SessionDao
import models.dao.UserDao
import models.entities.Session
import services.dto.SignInDTO
import utils.md5
import java.time.LocalDateTime

class AuthorizationService(
    private val userDao: UserDao = UserDao(),
    private val sessionDao: SessionDao = SessionDao(),
) {
    fun signIn(signInDto: SignInDTO): Result<Session> {
        val user = userDao.findUserByLogin(signInDto.email).getOrElse { e ->
            return Result.failure(e)
        }

        if (signInDto.password.md5() != user.password) {
            return Result.failure(PasswordWrongException())
        }
        return sessionDao
            .insert(Session(user = user, expiresAt = LocalDateTime.now().withHour(1)))
    }
}