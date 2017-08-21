package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
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

	companion object {
		val DATE_LINE_ITEMS_SIZE = 15
	}

    @GetMapping("/daily")
    fun getUserDailyTasks(principal: Principal): DailyDTO {
        val username = principal.name
        val dateLineItems: List<DatelineItemDTO> = taskService.getDateLineDates(DATE_LINE_ITEMS_SIZE, username)
        val taskList: List<DailyTask> = taskService.findByUsername(username)
        return DailyDTO(
				tasks = taskList.map { it.toTO() },
				dateLineItems = dateLineItems
		)

    }

    @PostMapping("/daily")
    fun createNewDailyTask(@RequestBody newTask: DailyTaskDTO, principal: Principal): ResponseEntity<IdResponse> {
        val savedTaskId = taskService.createDailyTask(newTask, principal.name)
        return ResponseEntity.ok(IdResponse(savedTaskId))
    }
}