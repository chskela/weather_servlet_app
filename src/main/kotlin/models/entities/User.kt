package models.entities

import jakarta.persistence.*

@Entity
@Table(name = "users", indexes = [Index(name = "idx_login", columnList = "login", unique = true)])
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "login", unique = true, nullable = false)
    val login: String = "",

    @Column(name = "password", nullable = false)
    val password: String = ""
)