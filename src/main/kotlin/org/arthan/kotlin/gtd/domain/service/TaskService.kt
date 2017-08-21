package org.arthan.kotlin.gtd.domain.service

import org.arthan.kotlin.gtd.domain.model.DailyTask
import org.arthan.kotlin.gtd.domain.repository.DailyTaskRepository
import org.arthan.kotlin.gtd.domain.repository.UserRepository
import org.arthan.kotlin.gtd.web.rest.dto.DateDTO
import org.arthan.kotlin.gtd.web.rest.dto.DailyTaskDTO
import org.arthan.kotlin.gtd.web.rest.dto.DatelineItemDTO
import org.arthan.kotlin.gtd.web.rest.dto.toDTO
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
        val dateService: DateService,
        val userRepository: UserRepository
) {
    fun findByUsername(username: String): List<DailyTask> {
        val tasks = dailyTaskRepository.findByUsername(username)
        return tasks
    }

    fun createDailyTask(newTask: DailyTaskDTO, username: String) {
        val userId = userRepository.findByUsername(username).id
        val taskToSave = DailyTask(name = newTask.name, userId = userId)
        dailyTaskRepository.save(taskToSave)
    }

    fun getDateLineDates(listSize: Int): List<DatelineItemDTO> {
		val dates = createDates(listSize)

		val dateLineItems = dates.map {
			DatelineItemDTO(it, emptyList(), false)
		}

        return dateLineItems
    }

	private fun createDates(
			listSize: Int): List<DateDTO> {
		val currentDate: LocalDate = dateService.getCurrentDate()
		var startDate = currentDate.minusDays(listSize.toLong() - 1)
		val dates = mutableListOf<DateDTO>()
		for (i in 1..listSize) {
			dates.add(startDate.toDTO())
			startDate = startDate.plusDays(1)
		}
		return dates
	}
}