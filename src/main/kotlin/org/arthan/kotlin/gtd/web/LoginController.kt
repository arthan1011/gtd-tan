package org.arthan.kotlin.gtd.web

import org.arthan.kotlin.gtd.domain.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ObjectError
import org.springframework.validation.Validator
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid
import javax.validation.constraints.Size

/**
 * Created by arthan on 4/14/17 .
 */

@Controller
class LoginController {

    @Autowired
    lateinit var userService: UserService

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.addValidators(NewUserFormValidator())
    }

    @PostMapping(
            value = "/registration",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun register(@Valid newUser: NewUserForm, bindingResult: BindingResult): String {

        if (bindingResult.hasErrors()) return "registration"

        userService.createUser(newUser)
        return "redirect:/login"
    }
}

@Component
class NewUserForm(
        @field:Size(min = 3, max = 30) var username: String,
        var password: String,
        var repeatedPassword: String
) {
    constructor():this("", "", "")

    override fun toString(): String {
        return "NewUserForm(username='$username', password='$password', repeatedPassword='$repeatedPassword')"
    }
}

class NewUserFormValidator : Validator {
    override fun supports(clazz: Class<*>?): Boolean {
        return clazz?.equals(NewUserForm::class.java)!!
    }

    override fun validate(target: Any?, errors: Errors?) {
        if (target is NewUserForm && errors != null) {
            validatePassword(target, errors)
        }
    }

    private fun validatePassword(form: NewUserForm, errors: Errors) {
        if (form.password != form.repeatedPassword) {
            errors.rejectValue("repeatedPassword", "valid.error.repeatedPassword", "You should repeat password")
        }
    }
}