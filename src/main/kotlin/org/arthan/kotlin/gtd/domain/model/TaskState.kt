package org.arthan.kotlin.gtd.domain.model

import java.time.LocalDate
import org.arthan.kotlin.gtd.domain.model.enums.TaskDateState

/**
 * Created by shamsiev on 29.08.2017 for gtd-tan.
 */
data class TaskState(
		val id: Long? = null,

		val taskId: Long? = null,

		val date: LocalDate? = null,

		val state: TaskDateState? = null
)