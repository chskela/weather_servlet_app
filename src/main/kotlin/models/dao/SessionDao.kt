package models.dao

import exception.SessionNotFoundException
import jakarta.persistence.EntityManager
import models.database.PersistenceUtil
import models.entities.Session
import java.time.LocalDateTime
import java.util.*

class SessionDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun findSessionById(uuid: UUID): Result<Session> {
        return try {
            val session = entityManager.find(Session::class.java, uuid)

            if (session != null) {
                Result.success(session)
            } else Result.failure(SessionNotFoundException("Session not found"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun insert(session: Session): Result<Session> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager.merge(session)
        }.onSuccess { txn.commit() }
            .onFailure { txn.rollback() }
    }

    fun removeSessionById(uuid: UUID): Result<Int> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager
                .createQuery("DELETE FROM Session WHERE id = :id")
                .setParameter("id", uuid)
                .executeUpdate()
        }.onSuccess {
            txn.commit()
            entityManager.clear()
        }
            .onFailure { txn.rollback() }
    }

    fun removeSessionExpiredAtTime(time: LocalDateTime): Result<Int> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager
                .createQuery("DELETE FROM Session WHERE expiresAt <= :time")
                .setParameter("time", time)
                .executeUpdate()
        }.onSuccess {
            txn.commit()
            entityManager.clear()
        }
            .onFailure { txn.rollback() }
    }
}