package models.dao

import exception.LocationExistsException
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import models.database.PersistenceUtil
import models.entities.Location
import models.entities.User
import org.hibernate.exception.ConstraintViolationException

class LocationDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun getAllLocationsByUserId(user: User): Result<List<Location>> {
        return runCatching {
            val query: TypedQuery<Location> = entityManager
                .createQuery("SELECT l FROM Location l WHERE l.user = :user", Location::class.java)
                .setParameter("user", user)
            query.resultList
        }
    }

    fun insert(location: Location): Result<Location> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager.merge(location)
        }.onSuccess { txn.commit() }
            .onFailure { e ->
                txn.rollback()
                if (e is ConstraintViolationException) {
                    throw LocationExistsException(location.name)
                }
            }
    }

    fun removeLocationByCoordinateForUser(location: Location): Result<Int> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager
                .createQuery(
                    "DELETE FROM Location " +
                            "WHERE latitude = :latitude " +
                            "AND longitude = :longitude " +
                            "AND user = :user"
                )
                .setParameter("latitude", location.latitude)
                .setParameter("longitude", location.longitude)
                .setParameter("user", location.user)
                .executeUpdate()
        }.onSuccess {
            txn.commit()
            entityManager.clear()
        }
            .onFailure { txn.rollback() }
    }
}