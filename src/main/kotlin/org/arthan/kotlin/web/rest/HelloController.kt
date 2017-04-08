package org.arthan.kotlin.web.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
* Created by arthan on 4/8/17 .
*/

@RestController
class HelloController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, stranger"
    }

}