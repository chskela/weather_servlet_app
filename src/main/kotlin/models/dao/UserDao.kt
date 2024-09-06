package models.dao

import exception.UserExistsException
import exception.UserNotExistsException
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.TypedQuery
import models.database.PersistenceUtil
import models.entities.User
import org.hibernate.exception.ConstraintViolationException

class UserDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun findUserByLogin(login: String): Result<User> {
        return try {
            val query: TypedQuery<User> = entityManager
                .createQuery("SELECT u FROM User u WHERE u.login = :login", User::class.java)
                .setParameter("login", login)
            Result.success(query.singleResult)
        } catch (e: Exception) {
            if (e is NoResultException) {
                Result.failure(UserNotExistsException(login))
            } else Result.failure(RuntimeException(e))
        }
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