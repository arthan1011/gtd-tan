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
		val DATE_LINE_ITEMS_SIZE = 29
	}

    @GetMapping("/daily")
    fun getUserDailyTasks(clientMetaData: ClientMetaData, principal: Principal): DailyDTO {
        val username = principal.name
		val hourOffset = clientMetaData.minuteOffset / 60
		val dateLineItems = taskService.getDateLineDates(DATE_LINE_ITEMS_SIZE, username, hourOffset)
        val taskList: List<DailyTask> = taskService.findByUsername(username)
        return DailyDTO(
				tasks = taskList.map { it.toTO() },
				dateLineItems = dateLineItems
		)

    }

    @PostMapping("/daily")
    fun createNewDailyTask(
			@RequestBody newTask: NewTaskDTO,
			clientMetaData: ClientMetaData,
			principal: Principal
	): ResponseEntity<IdResponse> {
        val savedTaskId = taskService.createDailyTask(newTask.name, principal.name, clientMetaData.minuteOffset / 60)
        return ResponseEntity.ok(IdResponse(savedTaskId))
    }

	@PostMapping("/daily/{id}/complete")
	fun completeTask(
			@PathVariable("id") taskId: Long,
			clientMetaData: ClientMetaData
	): ResponseEntity<String> {
		taskService.completeTask(taskId, clientMetaData.minuteOffset / 60)
		return ResponseEntity.ok("success")
	}
}