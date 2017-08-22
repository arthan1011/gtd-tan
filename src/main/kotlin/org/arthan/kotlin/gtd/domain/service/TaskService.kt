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
import java.time.ZoneId
import java.util.*

/**
 * Business logic for tasks
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

@Service
class TaskService @Autowired constructor(
        val dailyTaskRepository: DailyTaskRepository,
        var dateService: DateService,
        val userRepository: UserRepository
) {
    fun findByUsername(username: String): List<DailyTask> {
        val tasks = dailyTaskRepository.findByUsername(username)
        return tasks
    }

    fun createDailyTask(newTask: DailyTaskDTO, username: String): Long {
        val userId = userRepository.findByUsername(username).id
        val taskToSave = DailyTask(name = newTask.name, userId = userId, startDate = dateService.getDate())
        return dailyTaskRepository.save(taskToSave).id ?: throw ServiceException("daily task save error")
    }

    fun getDateLineDates(listSize: Int, username: String): List<DatelineItemDTO> {
		val dates = createDates(listSize)
		val tasks = findByUsername(username)
		val dateLineItems = dates.map { dateDTO ->
			val isToday = isToday(dateDTO)
			DatelineItemDTO(
					date = dateDTO,
					tasks = tasks.map { t -> DailyTaskDTO(
							name = null,
							id = t.id!!,
							completed = isCompleted(isToday, t.startDate!!, dateDTO)) },
					today = isToday
			)
		}

        return dateLineItems
    }

	private fun isToday(dateDTO: DateDTO): Boolean {
		val now = LocalDate.now()
		return now.year == dateDTO.year.toInt() &&
				now.monthValue == dateDTO.month.toInt() &&
				now.dayOfMonth == dateDTO.day.toInt()
	}

	private fun createDates(
			listSize: Int): List<DateDTO> {
		val currentDate = dateService.getLocalDate()
		var startDate = currentDate.minusDays(listSize.toLong() - 1)
		val dates = mutableListOf<DateDTO>()
		for (i in 1..listSize) {
			dates.add(startDate.toDTO())
			startDate = startDate.plusDays(1)
		}
		return dates
	}
}

internal fun isCompleted(isToday: Boolean, startDate: Date, dateDTO: DateDTO): Boolean? {
	if (isToday) {
		return null
	}
	val localDate = LocalDate.of(dateDTO.year.toInt(), dateDTO.month.toInt(), dateDTO.day.toInt())
	val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
	val start = startDate.toInstant()
	if (instant == start || instant.isAfter(start)) {
		return false
	}
	return null
}