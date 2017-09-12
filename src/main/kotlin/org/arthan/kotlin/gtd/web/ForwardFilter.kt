package org.arthan.kotlin.gtd.web

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by arthan on 28.07.2017. | Project gtd-tan
 */

class ForwardFilter : Filter {
    override fun destroy() {

    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        if (request is HttpServletRequest) {

            if (request.contextPath.startsWith("/rest") || request.contextPath.startsWith("/logout")) {
                chain.doFilter(request, response)
                return
            }

            val httpResponse = response as HttpServletResponse
            httpResponse.sendRedirect("/ui")
        } else {
            chain.doFilter(request, response)
        }
    }

    override fun init(filterConfig: FilterConfig?) {

    }
}