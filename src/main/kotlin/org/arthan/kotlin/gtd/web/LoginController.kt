package org.arthan.kotlin.gtd.web

import org.arthan.kotlin.gtd.domain.exception.UsernameExistsException
import org.arthan.kotlin.gtd.domain.service.UserService
import org.arthan.kotlin.gtd.web.validator.NewUserFormValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError
import org.springframework.validation.Validator
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Size

/**
 * Controller for users registration
 *
 * Created by arthan on 4/14/17 .
 */

@RestController
class LoginController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping(value = "/registration")
    fun register(@RequestBody newUser: NewUserForm): ResponseEntity<UserValidationReply> {

        val validationResult: UserValidationReply = validateNewUser(newUser)
        if (!validationResult.success) {
            return ResponseEntity.ok(validationResult)
        }

        userService.createUser(newUser)
        return ResponseEntity.ok(UserValidationReply(success = true, message = "You create a new user"))
    }

    private fun validateNewUser(newUser: NewUserForm): UserValidationReply {
        val userExists = userService.userExists(newUser.username)
        return if (userExists) {
            UserValidationReply(
                    success = false,
                    usernameMessage = "User with name '${newUser.username}' already exists")
        } else {
            UserValidationReply(success = true)
        }
    }
}

class NewUserForm(
        var username: String = "",
        var password: String = "",
        var repeatedPassword: String = ""
) {
    override fun toString(): String {
        return "NewUserForm(username='$username', password='$password', repeatedPassword='$repeatedPassword')"
    }
}

class UserValidationReply(
        var success: Boolean = false,
        var usernameMessage: String? = null,
        var passwordMessage: String? = null,
        var repeatedPasswordMessage: String? = null,
        var message: String? = null
)