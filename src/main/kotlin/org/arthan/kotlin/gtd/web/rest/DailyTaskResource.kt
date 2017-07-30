package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskTO
import org.arthan.kotlin.gtd.web.rest.dto.toTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal

/**
 * Created by arthan on 22.07.2017. | Project gtd-tan
 */

@RestController
@RequestMapping("/rest/task")
class DailyTaskResource @Autowired constructor(
        val taskService: TaskService
) {

    @GetMapping("/daily")
    fun getUserDailyTasks(principal: Principal): List<DailyTaskTO> {
        val name = principal.name
        val list: List<DailyTask> = taskService.findByUsername(name)
        return list.map { it.toTO() }
    }

    @PostMapping("/daily")
    fun createNewDailyTask( @RequestBody newTask: DailyTaskTO, principal: Principal) {
        taskService.createDailyTask(newTask, principal.name)
    }
}