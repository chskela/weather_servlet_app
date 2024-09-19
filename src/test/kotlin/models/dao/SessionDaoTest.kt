package models.dao

import exception.SessionNotFoundException
import jakarta.persistence.EntityManager
import models.database.TestContainerConfig
import models.entities.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.dto.AuthorizationDTO
import services.dto.toUser
import java.time.LocalDateTime
import java.util.*

class SessionDaoTest {
    val entityManager: EntityManager = TestContainerConfig.entityManagerFactory.createEntityManager()
    private val userDao = UserDao(entityManager)
    private val sessionDao = SessionDao(entityManager)

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
    fun `insert new Session should returned added Session`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val session = Session(user = addedUser, expiresAt = LocalDateTime.now().withHour(1))
        val addedSession = sessionDao.insert(session).getOrThrow()

        //then
        assertNotNull(addedSession.id)
        assertNotNull(addedSession.user)
        assertNotNull(addedSession.expiresAt)
        assertEquals(addedSession.user.login, candidate.email)
    }


    @Test
    fun `remove existing Session by UUID should returned removed 1`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val session = Session(user = addedUser, expiresAt = LocalDateTime.now().withHour(1))
        val addedSession = sessionDao.insert(session).getOrThrow()
        val result = sessionDao.removeSessionById(addedSession.id).getOrThrow()

        //then
        assertEquals(1, result)
    }


    @Test
    fun `find existing Session by UUID should returned Session`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val session = Session(user = addedUser, expiresAt = LocalDateTime.now().withHour(1))
        val addedSession = sessionDao.insert(session).getOrThrow()
        val result = sessionDao.findSessionById(addedSession.id).getOrThrow()

        //then
        assertNotNull(result.id)
        assertNotNull(result.user)
        assertNotNull(result.expiresAt)
        assertEquals(result.user.login, candidate.email)
    }

    @Test
    fun `find not existing Session by UUID should thrown SessionNotFoundException`() {
        //when
        val fakeUuid = UUID.randomUUID()

        //then
        assertThrows<SessionNotFoundException> { sessionDao.findSessionById(fakeUuid).getOrThrow() }
    }
}