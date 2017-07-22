package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by arthan on 22.07.2017. | Project gtd-tan
 */

@RestController
@RequestMapping("/rest/task")
class DailyTaskResource {

    @GetMapping("/daily")
    fun getUserDailyTasks(): List<DailyTaskTO> {

        return listOf(
                DailyTaskTO("Be cool"),
                DailyTaskTO("Look awesome"),
                DailyTaskTO("Don't break a promise")
        )
    }
}