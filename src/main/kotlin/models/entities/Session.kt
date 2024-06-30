package models.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "sessions")
class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "fk_locations_users"))
    val user: User = User(),

    @Column(name = "ExpiresAt")
    val expiresAt: LocalDateTime = LocalDateTime.now(),
)