package org.arthan.kotlin.gtd.web.filter

import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by arthan on 4/12/17 .
 */

@Component
class AppRedirectFilter : Filter {
    override fun init(filterConfig: FilterConfig?) {}

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        if (request is HttpServletRequest) {
            val path = request.servletPath

            if (path == "/app") {
                if (response is HttpServletResponse) {
                    response.sendRedirect("/app/")
                    return
                }
            }

            if (path.startsWith("/app") &&
                    path != "/app/index.html" &&
                    !path.startsWith("/app/assets")) {
                request.getRequestDispatcher("/app/index.html")?.forward(request, response)
            } else {
                chain?.doFilter(request, response)
            }
        }
    }

    override fun destroy() {}
}