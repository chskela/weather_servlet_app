package models.dao

import jakarta.persistence.EntityManager
import models.database.PersistenceUtil
import models.entities.User

class UserDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun findUserById(userId: Int): Result<User> {
        return runCatching { entityManager.find(User::class.java, userId) }
    }

    fun insert(user: User): Result<User> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager.merge(user)
        }.onSuccess { txn.commit() }
            .onFailure { txn.rollback() }
    }
}