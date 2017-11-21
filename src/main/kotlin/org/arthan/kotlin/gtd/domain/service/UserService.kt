package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.exception.UsernameExistsException
import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.NewUserForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Business logic for application users
 *
 * Created by arthan on 4/14/17 .
 */

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
	lateinit var passwordEncoder: PasswordEncoder

    fun createUser(newUser: NewUserForm) {

        val userToCreate = User(
                username = newUser.username,
                password = passwordEncoder.encode(newUser.password),
                role = "USER")
        userRepository.save(userToCreate)
    }

    fun userExists(username: String): Boolean {
        return userRepository.usernameExists(username)
    }

    fun checkCredentials(username: String, password: String): Long? {
        val savedUser: User = userRepository.findByUsername(username) ?: return null
        val correctUser = passwordEncoder.matches(password, savedUser.password)
        return if (correctUser) { savedUser.id } else { null }
    }

}