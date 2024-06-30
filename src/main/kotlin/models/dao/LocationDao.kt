package models.dao

import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import models.database.PersistenceUtil
import models.entities.Location

class LocationDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun getAllLocationsByUserId(userId: Long): Result<List<Location>> {
        return runCatching {
            val query: TypedQuery<Location> = entityManager
                .createQuery("SELECT l FROM Locations l WHERE l.userId = :userId", Location::class.java)
                .setParameter("userId", userId)
            query.resultList
        }
    }

    fun insert(location: Location): Result<Location> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager.merge(location)
        }.onSuccess { txn.commit() }
            .onFailure { txn.rollback() }
    }
}