package models.dao

import exception.LocationExistsException
import jakarta.persistence.EntityManager
import models.database.TestContainerConfig
import models.entities.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.dto.AuthorizationDTO
import services.dto.toUser
import kotlin.random.Random

class LocationDaoTest {
    private val entityManager: EntityManager = TestContainerConfig().entityManagerFactory.createEntityManager()
    private val userDao = UserDao(entityManager)
    private val locationDao = LocationDao(entityManager)

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
    fun `insert new Location should return Location`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val latitude = 50.0
        val longitude = 60.0
        val locationName = "Test"
        val location = locationDao.insert(
            Location(
                name = locationName,
                user = addedUser,
                latitude = latitude,
                longitude = longitude
            )
        ).getOrThrow()

        //then
        assertNotNull(location)
        assertEquals(location.name, locationName)
        assertEquals(location.user, addedUser)
        assertEquals(location.latitude, latitude)
        assertEquals(location.longitude, longitude)
    }

    @Test
    fun `insert similar Location for the same user should throw exception`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val latitude = 50.0
        val longitude = 60.0
        val locationName = "Test"
        locationDao.insert(
            Location(
                name = locationName,
                user = addedUser,
                latitude = latitude,
                longitude = longitude
            )
        )

        //then
        assertThrows<LocationExistsException> {
            locationDao.insert(
                Location(
                    name = locationName,
                    user = addedUser,
                    latitude = latitude,
                    longitude = longitude
                )
            ).getOrThrow()
        }
    }

    @Test
    fun `get all locations should return list of locations`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        repeat(10) {
            locationDao.insert(
                Location(
                    user = addedUser,
                    latitude = Random.nextDouble(),
                    longitude = Random.nextDouble(),
                )
            )
        }
        val locations = locationDao.getAllLocationsByUserId(addedUser).getOrThrow()

        //then
        assertNotNull(locations)
        assertEquals(locations.size, 10)
    }

    @Test
    fun `remove Location should returned removed 1`() {
        //when
        val addedUser = userDao.insert(candidate.toUser()).getOrThrow()
        val latitude = 50.0
        val longitude = 60.0
        val locationName = "Test"
        val location = Location(
            name = locationName,
            user = addedUser,
            latitude = latitude,
            longitude = longitude
        )
        locationDao.insert(location)

        val result = locationDao.removeLocationByCoordinateForUser(location).getOrThrow()

        //then
        assertEquals(result, 1)
    }
}