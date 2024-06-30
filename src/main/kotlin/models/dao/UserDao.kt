package models.dao

import exception.UserExistsException
import jakarta.persistence.EntityManager
import models.database.PersistenceUtil
import models.entities.User
import org.hibernate.exception.ConstraintViolationException

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
            .onFailure { e ->
                txn.rollback()
                if (e is ConstraintViolationException) {
                    throw UserExistsException(user.login)
                }
            }
    }
}