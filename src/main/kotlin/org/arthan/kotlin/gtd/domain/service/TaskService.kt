package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.model.TaskState
import org.arthan.kotlin.gtd.domain.model.enums.TaskDateState
import org.arthan.kotlin.gtd.domain.model.enums.TaskType
import org.arthan.kotlin.gtd.domain.repository.DailyTaskRepository
import org.arthan.kotlin.gtd.domain.repository.TaskStateRepository
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.domain.service.exception.ServiceException
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.arthan.kotlin.gtd.web.rest.dto.DatelineItemDTO
import org.arthan.kotlin.gtd.web.rest.dto.toDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Business logic for tasks
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

@Service
class TaskService @Autowired constructor(
        val dailyTaskRepository: DailyTaskRepository,
        val taskStateRepository: TaskStateRepository,
        var dateService: DateService,
        val userRepository: UserRepository
) {

	companion object {
		private val logger = LoggerFactory.getLogger("USER_TASKS")
	}

    fun findByUsername(username: String): List<DailyTask> {
        val tasks = dailyTaskRepository.findByUsername(username)
        return tasks
    }

    fun createDailyTask(newTaskName: String, newTaskType: String, username: String, offset: Int): Long {
        val userId = userRepository.findByUsername(username).id

		val taskType: TaskType
		try {
			taskType = TaskType.valueOf(newTaskType.toUpperCase())
		} catch(e: IllegalArgumentException) {
			throw ServiceException("incorrect task type '$newTaskType'")
		}

		val taskToSave = DailyTask(
				name = newTaskName,
				userId = userId,
				startDate = dateService.getDay(offset),
				type = taskType
		)
		val taskId = dailyTaskRepository.save(taskToSave).id ?: throw ServiceException("daily task save error")
		logger.debug("Task #$taskId with name \"$newTaskName\" for user \"$username\" was created")
		return taskId
    }

    fun getDateLineDates(listSize: Int, username: String, offset: Int): List<DatelineItemDTO> {
		val dates: List<LocalDate> = createDates(listSize, offset)
		val tasks = findByUsername(username)
		val taskStates = getTaskStates(dates.first(), dates.last(), username)
		val dateLineItems = dates.map { date ->
			val today = dateService.getDay(offset)
			DatelineItemDTO(
					date = date.toDTO(),
					tasks = tasks.map { t ->
						val dateBasedState = isCompleted(today, t.startDate!!, date)
						val taskState = taskStates.filter { it.taskId == t.id }.find { it.date == date }
						val completed = findDateItemState(taskState) ?: dateBasedState
						DailyTaskDTO(
							id = t.id!!,
							completed = completed)
					},
					today = today == date
			)
		}

        return dateLineItems
    }

	internal fun findDateItemState(taskState: TaskState?): Boolean? {
		if (taskState == null) {
			return null
		}

		return when (taskState.state) {
			TaskDateState.COMPLETED -> true
			TaskDateState.FAILED -> false
			null -> throw IllegalStateException("Task state should not be null")
		}
	}

	/**
	 * Returns LocalDate list size of listSize with today in the middle
	 *
	 * @param listSize result list size
	 * @param timeZoneOffset hours timezone offset
	 *
	 */
	internal fun createDates(listSize: Int, timeZoneOffset: Int): List<LocalDate> {
		val currentDate = dateService.getDay(timeZoneOffset)
		val startDateOffset = (listSize.toLong() / 2)
		var startDate = currentDate.minusDays(startDateOffset)
		val dates = mutableListOf<LocalDate>()
		for (i in 1..listSize) {
			dates.add(startDate)
			startDate = startDate.plusDays(1)
		}
		return dates
	}

	internal fun isCompleted(today: LocalDate, startDate: LocalDate, date: LocalDate): Boolean? {
		if (date == today) {
			return null
		}
		val dateIsAfterCreation: Boolean = date == startDate || date.isAfter(startDate)
		if (dateIsAfterCreation && date.isBefore(today)) {
			return false
		}
		return null
	}

	fun getTaskStates(from: LocalDate, to: LocalDate, username: String): List<TaskState> {
		return taskStateRepository.findInRangeForUsername(from, to, username)
	}

	fun completeTask(taskId: Long, offset: Int) {
		val state = TaskState(
				taskId = taskId,
				date = dateService.getDay(offset),
				state = TaskDateState.COMPLETED
		)
		taskStateRepository.save(state)
	}

	// TODO: move validation for user rights on task to repository (v0.1.4)
	fun editTaskName(taskId: Long, newName: String, username: String) {
		val task = dailyTaskRepository.findOne(taskId)
		val user = userRepository.findByUsername(username)

		if (task.userId != user.id) {
			return // TODO: throw exception that will result in response code 401
		}

		val oldName = task.name
		task.name = newName
		dailyTaskRepository.save(task)
		logger.debug("Name for task #${task.id} was changed from \"$oldName\" to \"$newName\"")
	}

	fun changeTaskState(taskId: Long, newState: Boolean, username: String, offset: Int) {
		val task = dailyTaskRepository.findOne(taskId)
		val user = userRepository.findByUsername(username)

		if (task.userId != user.id) {
			return // TODO: throw exception that will result in response code 401
		}

		val state = if (newState) {
			TaskState(taskId = taskId, date = dateService.getDay(offset), state = TaskDateState.COMPLETED)
		} else {
			TaskState(taskId = taskId, date = dateService.getDay(offset), state = TaskDateState.FAILED)
		}
		taskStateRepository.save(state)

		logger.debug("Task state for task #${task.id} \"${task.name}\" was changed to \"$newState\"")
	}
}
