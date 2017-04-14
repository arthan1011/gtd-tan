package org.arthan.kotlin.gtd.domain.repository

import org.arthan.kotlin.gtd.domain.model.User
import org.springframework.data.repository.CrudRepository

/**
 * Created by arthan on 4/9/17 .
 */


interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User
}