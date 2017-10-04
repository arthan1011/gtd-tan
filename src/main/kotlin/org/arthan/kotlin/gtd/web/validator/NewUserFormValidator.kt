package org.arthan.kotlin.gtd.web.validator

import org.arthan.kotlin.gtd.domain.service.UserService
import org.arthan.kotlin.gtd.web.NewUserForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * Validator for user registration form
 *
 * Created by arthan on 31.08.2017. | Project gtd-tan
 */

@Component
open class NewUserFormValidator
@Autowired constructor(
		val userService: UserService
): Validator {
	override fun supports(clazz: Class<*>?): Boolean {
		return clazz?.equals(NewUserForm::class.java)!!
	}

	override fun validate(target: Any?, errors: Errors) {
		if (target is NewUserForm) {
			validatePassword(target, errors)
			validateUsername(target.username, errors)
		}
	}

	private fun validateUsername(username: String, errors: Errors) {
		if (userService.userExists(username)) {
			errors.rejectValue("username", "valid.error.usernameExists", "Username '$username' already exists!")
		}
		if (!isCorrectCredentials(username)) {
			errors.rejectValue("username", "valid.error.incorrectSymbols", "Username '$username' contains unallowed symbols!")
		}
	}

	private fun validatePassword(form: NewUserForm, errors: Errors) {
		if (form.password != form.repeatedPassword) {
			errors.rejectValue("repeatedPassword", "valid.error.repeatedPassword", "You should repeat password!")
		}
		if (!isCorrectCredentials(form.password)) {
			errors.rejectValue("repeatedPassword", "valid.error.incorrectSymbols", "Password contains unallowed symbols!")
		}
	}

	fun isCorrectCredentials(credential: String): Boolean {
		return "\\w+".toRegex().matches(credential)
	}
}