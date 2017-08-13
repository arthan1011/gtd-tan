package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.arthan.kotlin.gtd.web.rest.dto.DateDTO
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
    fun getUserDailyTasks(principal: Principal): Map<String, Any> {
        val name = principal.name
        val dates: List<DateDTO> = taskService.getDateLineDates()
        val list: List<DailyTask> = taskService.findByUsername(name)
        val tasks = list.map { it.toTO() }
        return mapOf(
                "meta" to mapOf("dates" to dates),
                "tasks" to tasks
        )
    }

    @PostMapping("/daily")
    fun createNewDailyTask(@RequestBody newTask: DailyTaskDTO, principal: Principal) {
        taskService.createDailyTask(newTask, principal.name)
    }
}