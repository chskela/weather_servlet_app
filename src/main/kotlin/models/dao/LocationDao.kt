package models.dao

import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import models.database.PersistenceUtil
import models.entities.Location
import models.entities.User

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
            .onFailure { txn.rollback() }
    }
}