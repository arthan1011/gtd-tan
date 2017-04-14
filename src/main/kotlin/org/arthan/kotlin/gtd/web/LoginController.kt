package org.arthan.kotlin.gtd.web

import org.arthan.kotlin.gtd.domain.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Created by arthan on 4/14/17 .
 */

@Controller
@RequestMapping("/user")
class LoginController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping(
            value = "/registration",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun register(newUser: NewUserForm): String {

        userService.createUser(newUser)

        return "redirect:/login"
    }
}

class NewUserForm(var username: String, var password: String, var repeatedPassword: String) {
    constructor():this("", "", "")

    override fun toString(): String {
        return "NewUserForm(username='$username', password='$password', repeatedPassword='$repeatedPassword')"
    }


}