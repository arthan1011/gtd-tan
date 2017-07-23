package org.arthan.kotlin.gtd.domain.model

import javax.persistence.*

/**
 * Entity for registered application users
 *
 * Created by arthan on 4/9/17 .
 */

@Entity
@Table(name = "users")
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