package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.NewUserForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by arthan on 4/14/17 .
 */

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun createUser(newUser: NewUserForm) {

        val userToCreate = User(
                username = newUser.username,
                password = newUser.password,
                role = "USER")
        userRepository.save(userToCreate)
    }

}