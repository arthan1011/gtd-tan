package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.web.rest.dto.ClientMetaData
import org.arthan.kotlin.gtd.web.rest.dto.TIME_OFFSET_HEADER
import org.springframework.core.MethodParameter
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Resolves certain http headers as ClientMetaData object for rest controllers argument
 * Created by shamsiev on 25.08.2017 for gtd-tan.
 */
class ClientMetaDataResolver : HandlerMethodArgumentResolver {
	override fun supportsParameter(parameter: MethodParameter?): Boolean {
		return parameter!!.parameterType == ClientMetaData::class.java
	}

	override fun resolveArgument(parameter: MethodParameter?, mavContainer: ModelAndViewContainer?,
								 webRequest: NativeWebRequest?, binderFactory: WebDataBinderFactory?): Any {
		val header: String = webRequest!!.getHeader(TIME_OFFSET_HEADER) ?:
				throw ServletRequestBindingException("\"AX-GTD-Minute-Offset\" header was not present")
		val offset = -header.toInt()
		return ClientMetaData(offset)
	}
}