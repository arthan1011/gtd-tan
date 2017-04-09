package org.arthan.kotlin.domain.repository

import org.arthan.kotlin.domain.model.User
import org.springframework.data.repository.CrudRepository

/**
 * Created by arthan on 4/9/17 .
 */


interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User
}