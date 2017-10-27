package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.service.exception.ForbiddenException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Created by arthan on 27.10.2017. | Project gtd-tan
 */

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

	@ResponseStatus(HttpStatus.FORBIDDEN, reason = "not allowed")
	@ExceptionHandler(value = ForbiddenException::class)
	fun interceptForbidden() {
		// empty on purpose
	}
}