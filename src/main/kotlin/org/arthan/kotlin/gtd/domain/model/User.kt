package org.arthan.kotlin.gtd.domain.model

import javax.persistence.*

/**
 * Entity for registered application users
 *
 * Created by arthan on 4/9/17 .
 */

@Entity
@Table(name = "users")
@NamedQueries(
        NamedQuery(
                name = "User.usernameExists",
                query = "select case when (count (u) > 0) then true else false end " +
                        "from User u " +
                        "where u.username = ?1"
        ),
        NamedQuery(
                name = "User.userExists",
                query = "select case when (count (u) > 0) then true else false end " +
                        "from User u " +
                        "where u.username = ?1 and u.password = ?2"
        )
)

class User(
        var username: String,
        var password: String,
        var role: String,
        var enabled: Boolean = true
) {
    constructor(): this("", "", "")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    var id: Long = 0

    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password', enabled=$enabled, role='$role')"
    }


}