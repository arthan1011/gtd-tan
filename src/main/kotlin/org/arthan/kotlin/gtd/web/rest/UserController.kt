package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.User
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.UserService
import org.arthan.kotlin.gtd.web.rest.dto.AuthDTO
import org.arthan.kotlin.gtd.web.rest.dto.UserCredentialsDTO
import org.arthan.kotlin.gtd.web.rest.resolver.Credentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.authentication.UserCredentials
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

/**
 * Authentication related controller
 * Created by arthan on 4/9/17 .
 */

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var userService: UserService

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: String): User {

        val user = userRepository.findOne(userId.toLong())
        return user
    }

    @GetMapping("/hello")
    fun getWelcome(credentials: Credentials): String {
        return "{\"name\": \"User\"}"
    }

    @PostMapping("/check")
    fun checkUserCredentials(
            @RequestBody userCredentials: UserCredentialsDTO
    ): ResponseEntity<AuthDTO> {

        if (userCredentials.username == null || userCredentials.password == null) {
            return ResponseEntity.badRequest().build()
        }

        val userId: Long? = userService.checkCredentials(
                username = userCredentials.username!!,
                password = userCredentials.password!!
        )

        val res = if (userId == null) {
            AuthDTO(success = false, message = "Incorrect username or password")
        } else {
            AuthDTO(success = true, clientId = userId)
        }

        return ResponseEntity.ok(res)
    }
}