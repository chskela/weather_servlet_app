package models.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sessions")
class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "fk_locations_users"))
    val user: User,

    @Column(name = "ExpiresAt")
    val expiresAt: LocalDateTime,
    )