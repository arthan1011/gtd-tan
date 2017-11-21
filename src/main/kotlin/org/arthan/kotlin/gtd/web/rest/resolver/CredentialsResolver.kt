package org.arthan.kotlin.gtd.web.rest.resolver

import org.springframework.core.MethodParameter
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Created by arthan on 21.11.2017. | Project gtd-tan
 */
class CredentialsResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter?): Boolean {
        return parameter!!.parameterType == Credentials::class.java
    }

    override fun resolveArgument(
            parameter: MethodParameter?,
            mav: ModelAndViewContainer?,
            req: NativeWebRequest?,
            binderFactory: WebDataBinderFactory?
    ): Any {
        val userID: String = req!!.getHeader("AX-GTD-User-ID") ?:
                throw ServletRequestBindingException("\"AX-GTD-User-ID\" header was not present")
        return Credentials(userID.toLong())
    }
}