package models.dao

import exception.UserExistsException
import exception.UserNotExistsException
import jakarta.persistence.EntityManager
import models.database.TestContainerConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.dto.AuthorizationDTO
import services.dto.toUser

class UserDaoTest {

    private val entityManager: EntityManager = TestContainerConfig().entityManagerFactory.createEntityManager()
    private val userDao = UserDao(entityManager)

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
    fun `insert new User should returned added User`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()

        //then
        assertNotNull(addedUser.id)
        assertNotNull(addedUser.login)
        assertEquals(candidate.email, addedUser.login)
    }

    @Test
    fun `insert existing User should thrown UserExistsException`() {
        //when
        userDao.insert(candidate.toUser())

        //then
        assertThrows<UserExistsException> { userDao.insert(candidate.toUser()).getOrThrow() }
    }


    @Test
    fun `find User by login should return User`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val findUser = userDao.findUserByLogin(addedUser.login).getOrThrow()

        //then
        assertEquals(candidate.email, findUser.login)
    }


    @Test
    fun `find not existing User by login should throw UserNotExistsException`() {
        //when
        val fakeLogin = "fakeLogin"

        //then
        assertThrows<UserNotExistsException> { userDao.findUserByLogin(fakeLogin).getOrThrow() }
    }
}
