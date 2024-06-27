package models.dao

import jakarta.persistence.EntityManager
import models.database.PersistenceUtil
import models.entities.Session
import java.util.*

class SessionDao(
    private val entityManager: EntityManager = PersistenceUtil.entityManagerFactory.createEntityManager()
) {
    fun findSessionById(uuid: UUID): Result<Session> {
        return runCatching { entityManager.find(Session::class.java, uuid) }
    }

    fun insert(session: Session): Result<Session> {
        val txn = entityManager.transaction

        return runCatching {
            txn.begin()
            entityManager.merge(session)
        }.onSuccess { txn.commit() }
            .onFailure { txn.rollback() }
    }
}