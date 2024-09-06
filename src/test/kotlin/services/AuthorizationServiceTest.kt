package services

import exception.PasswordWrongException
import exception.SessionNotFoundException
import exception.UserExistsException
import exception.UserNotExistsException
import jakarta.persistence.EntityManager
import models.dao.SessionDao
import models.dao.UserDao
import models.database.TestContainerConfig
import models.entities.Session
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.dto.AuthorizationDTO
import services.dto.toUser
import utils.md5
import java.time.LocalDateTime

class AuthorizationServiceTest {
    private val entityManager: EntityManager = TestContainerConfig.entityManagerFactory.createEntityManager()
    private val userDao = UserDao(entityManager)
    private val sessionDao = SessionDao(entityManager)
    private val authorizationService = AuthorizationService(userDao, sessionDao)

    private val candidate: AuthorizationDTO = AuthorizationDTO("test@gmail.com", "password")

    @BeforeEach
    fun setup() {
        val txn = entityManager.transaction
        try {
            txn.begin()
            entityManager.createQuery("DELETE FROM Session").executeUpdate()
            entityManager.createQuery("DELETE FROM Location").executeUpdate()
            entityManager.createQuery("DELETE FROM User").executeUpdate()
            txn.commit()
        } catch (e: Exception) {
            txn.rollback()
        }
    }

    @Test
    fun `test signIn with correct credentials`() {
        //when
        val insertedUser = userDao.insert(candidate.copy(password = candidate.password).toUser()).getOrThrow()
        val result = authorizationService.signIn(candidate).getOrThrow()

        //then
        assertTrue(result.user == insertedUser)
    }

    @Test
    fun `test signIn with incorrect login should throw UserNotExistsException`() {
        //when
        val authorizationDto = AuthorizationDTO("test", "test")

        // then
        assertThrows<UserNotExistsException> { authorizationService.signIn(authorizationDto).getOrThrow() }
    }

    @Test
    fun `test signIn with incorrect password should throw PasswordWrongException`() {
        //when
        userDao.insert(candidate.copy(password = candidate.password).toUser())
        val authorizationDto = candidate.copy(password = "wrongPassword")

        // then
        assertThrows<PasswordWrongException> { authorizationService.signIn(authorizationDto).getOrThrow() }
    }

    @Test
    fun `test signUp with correct credentials`() {
        //when
        val result = authorizationService.signUp(candidate).getOrThrow()
        val user = userDao.findUserByLogin(candidate.email).getOrThrow()

        //then
        assertTrue(result.user.login == candidate.email)
        assertTrue(user.login == candidate.email)
    }

    @Test
    fun `test signUp with existed login should throw UserExistsException`() {
        //when
        userDao.insert(candidate.copy(password = candidate.password.md5()).toUser())

        //then
        assertThrows<UserExistsException> { authorizationService.signUp(candidate).getOrThrow() }
    }

    @Test
    fun `test logout`() {
        //when
        val user = userDao.insert(candidate.copy(password = candidate.password.md5()).toUser()).getOrThrow()
        val result = sessionDao.insert(
            Session(user = user, expiresAt = LocalDateTime.now().withHour(1))
        ).getOrThrow()
        authorizationService.logout(result.id)

        //then
        assertThrows<SessionNotFoundException> { sessionDao.findSessionById(result.id).getOrThrow() }
    }
}
