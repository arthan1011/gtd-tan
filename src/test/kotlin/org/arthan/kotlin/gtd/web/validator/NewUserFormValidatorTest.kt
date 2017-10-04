package org.arthan.kotlin.gtd.web.validator

import org.arthan.kotlin.gtd.domain.service.UserService
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by arthan on 04.10.2017. | Project gtd-tan
 */
class NewUserFormValidatorTest {

	@Test
	fun shouldCheckAllowedSymbols() {
		val validator = NewUserFormValidator(UserService())
		assertTrue("Should permit allowed symbols", validator.isCorrectCredentials("Test_name123"))
		assertFalse(
				"Should not permit restricted symbols",
				validator.isCorrectCredentials("Помогите, Меня заперли в комплюктере!"))
	}
}