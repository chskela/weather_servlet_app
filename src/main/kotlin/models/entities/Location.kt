package models.entities

import jakarta.persistence.*

@Entity
@Table(name = "locations")
class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "fk_locations_users"))
    val user: User = User(),

    @Column(name = "latitude")
    val latitude: Double = 0.0,

    @Column(name = "longitude")
    val longitude: Double = 0.0,
)


