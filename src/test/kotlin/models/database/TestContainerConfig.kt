package models.database

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceUnit
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertTrue

@Testcontainers
class TestContainerConfig {
    private val config: MutableMap<String, String> = HashMap()

    @Container
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres")

    init {
        postgres.start()

        val jdbcUrl: String = postgres.jdbcUrl
        val jdbcUser: String = postgres.username
        val jdbcPassword: String = postgres.password

        config["jakarta.persistence.jdbc.url"] = jdbcUrl
        config["jakarta.persistence.jdbc.user"] = jdbcUser
        config["jakarta.persistence.jdbc.password"] = jdbcPassword
        config["hibernate.hbm2ddl.auto"] = "create-drop"
    }

    @Test
    fun test_running() {
        assertTrue(postgres.isRunning)
    }

    @delegate:PersistenceUnit(name = "persistence")
    val entityManagerFactory: EntityManagerFactory by lazy {
        Persistence.createEntityManagerFactory("persistence", config)
    }
}
