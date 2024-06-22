package models.entities

import jakarta.persistence.*

@Entity
@Table(name="users")
class User (
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    val id: Long? = null,

    @Column(name="login")
    val login: String = "",

    @Column(name="login")
    val password: String = ""
)