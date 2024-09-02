package models.entities

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(
    name = "locations",
    uniqueConstraints = [UniqueConstraint(
        name = "location_unique_for_user",
        columnNames = [
            "latitude",
            "longitude",
            "user"
        ]
    )]
)
class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "fk_locations_users"))
    val user: User = User(),

    @Column(name = "latitude")
    val latitude: Double = 0.0,

    @Column(name = "longitude")
    val longitude: Double = 0.0,
)


