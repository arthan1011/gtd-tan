package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by arthan on 4/9/17 .
 */

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: String): String {

        val user = userRepository.findOne(userId.toLong())
        return user.toString()
    }
}