package org.arthan.kotlin.gtd.domain.model

import java.time.LocalDate
import org.arthan.kotlin.gtd.domain.model.enums.TaskDateState
import javax.persistence.*

/**
 * Entity for task date state
 *
 * Created by shamsiev on 29.08.2017 for gtd-tan.
 */
@Entity
@Table(name = "task_state")
// TODO:
@NamedQuery(
		name = "TaskState.findInRangeForUsername",
		query = "select ts " +
				"from TaskState ts " +
				"where ts.date >= ?1 and " +
				"ts.date <= ?2 and " +
				"ts.taskId in (" +
				"select td.id " +
				"from DailyTask td, User u " +
				"where td.userId = ?3)"
)
data class TaskState(
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Long? = null,

		@Column(name = "task_id")
		val taskId: Long? = null,

		val date: LocalDate? = null,

		@Enumerated(EnumType.STRING)
		val state: TaskDateState? = null
)