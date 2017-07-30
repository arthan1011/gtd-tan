package org.arthan.kotlin.gtd.web

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * Created by arthan on 28.07.2017. | Project gtd-tan
 */

class ForwardFilter : Filter {
    override fun destroy() {

    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        if (request is HttpServletRequest) {
            request.getRequestDispatcher("/ui").forward(request, response)
        } else {
            chain.doFilter(request, response)
        }
    }

    override fun init(filterConfig: FilterConfig?) {

    }
}