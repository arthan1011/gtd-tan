package org.arthan.kotlin.domain.model

import javax.persistence.*

/**
 * Created by arthan on 4/9/17 .
 */

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userid")
    var id: Long = 0

    var username: String = ""

    var password: String = ""

    var enabled: Boolean = false

    override fun toString(): String {
        return "User(id=$id, username='$username', enabled=$enabled)"
    }

}