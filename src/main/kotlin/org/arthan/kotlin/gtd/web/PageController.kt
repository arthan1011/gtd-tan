package org.arthan.kotlin.gtd.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Created by arthan on 4/9/17 .
 */

@Controller
class PageController {

    @Value("\${message.test}")
    var message: String = "d"

    @GetMapping("/")
    fun main(model: MutableMap<String, Any>): String {
        model.put("msg", message)
        return "main"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/signup")
    fun singUp(): String {
        return "registration"
    }
}