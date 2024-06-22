package models.entities

import jakarta.persistence.*

@Entity
@Table(name="users", indexes = [Index(name = "idx_login", columnList = "login", unique = true)])
class User (
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    val id: Long? = null,

    @Column(name="login", unique = true)
    val login: String,

    @Column(name="login")
    val password: String
)