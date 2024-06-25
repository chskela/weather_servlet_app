package models.database

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

object PersistenceUtil {
    private var jdbcUrl: String = System.getenv("JAKARTA_PERSISTENCE_JDBC_URL")
    private var jdbcUser: String = System.getenv("JAKARTA_PERSISTENCE_JDBC_USER")
    private var jdbcPassword: String = System.getenv("JAKARTA_PERSISTENCE_JDBC_PASSWORD")
    private var hibernateDdlAuto: String = System.getenv("HIBERNATE_DDL_AUTO")
    private var config: MutableMap<String, String> = mutableMapOf()

    init {
        config["jakarta.persistence.jdbc.url"] = jdbcUrl
        config["jakarta.persistence.jdbc.user"] = jdbcUser
        config["jakarta.persistence.jdbc.password"] = jdbcPassword
        config["hibernate.hbm2ddl.auto"] = hibernateDdlAuto
    }

    val entityManagerFactory: EntityManagerFactory by lazy {
        Persistence.createEntityManagerFactory("persistence", config)
    }
}