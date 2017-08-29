package org.arthan.kotlin.gtd.domain.repository

import org.arthan.kotlin.gtd.domain.model.TaskState
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate

/**
 * Repository for TaskState Entity
 *
 * Created by shamsiev on 29.08.2017 for gtd-tan.
 */
interface TaskStateRepository : CrudRepository<TaskState, Long> {
	fun findInRangeForUsername(from: LocalDate, to: LocalDate, username: String): List<TaskState>
}