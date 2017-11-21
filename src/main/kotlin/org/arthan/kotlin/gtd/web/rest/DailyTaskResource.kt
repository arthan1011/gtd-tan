package org.arthan.kotlin.gtd.web.rest

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.service.TaskService
import org.arthan.kotlin.gtd.web.rest.dto.*
import org.arthan.kotlin.gtd.web.rest.resolver.ClientMetaData
import org.arthan.kotlin.gtd.web.rest.resolver.Credentials
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
    fun getUserDailyTasks(clientMetaData: ClientMetaData, credentials: Credentials): DailyDTO {
        val userId = credentials.userId
		val hourOffset = clientMetaData.minuteOffset / 60
		val dateLineItems = taskService.getDateLineDates(DATE_LINE_ITEMS_SIZE, userId, hourOffset)
        val taskList: List<DailyTask> = taskService.findByUserId(userId)
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

        val savedTaskId = taskService.createDailyTask(
				newTaskName = newTask.name,
				newTaskType = newTask.type,
				username = principal.name,
				offset = clientMetaData.minuteOffset / 60
		)
        return ResponseEntity.ok(IdResponse(savedTaskId))
    }

	@PutMapping("/daily/{id}/state")
	fun changeTaskDateItemState(
			@PathVariable("id") taskId: Long,
			@RequestBody newState: String,
			clientMetaData: ClientMetaData,
			principal: Principal
	): ResponseEntity<String> {

		val booleanState: Boolean = when (newState) {
			"failed" -> false
			"done" -> true
			else -> return ResponseEntity.badRequest().body("incorrect state for task '$newState'")
		}

		taskService.changeTaskState(taskId, booleanState, principal.name, clientMetaData.minuteOffset / 60)

		return ResponseEntity.ok("success")
	}

	@PutMapping("/daily/{id}/name")
	fun editTask(
			@PathVariable("id") taskId: Long,
			@RequestBody map: Map<String, String>,
			principal: Principal
	): ResponseEntity<String> {

		val name: String = map["name"] ?:
				return ResponseEntity.badRequest().body("You should specify task name")

		taskService.editTaskName(taskId, name, principal.name)

		return ResponseEntity.ok("success")
	}
}