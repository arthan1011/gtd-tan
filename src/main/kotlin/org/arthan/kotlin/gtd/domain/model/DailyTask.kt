package org.arthan.kotlin.gtd.domain.model

import org.arthan.kotlin.gtd.domain.model.enums.TaskType
import java.time.LocalDate
import java.util.*
import javax.persistence.*

/**
 * Entity for user daily tasks
 *
 * Created by arthan on 23.07.2017. | Project gtd-tan
 */

@Entity
@Table(name = "daily_task")
@SecondaryTable(
        name = "pomodoro_task_intervals",
        pkJoinColumns = arrayOf(PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")))
@NamedQueries(
        NamedQuery(
                name = "DailyTask.findByUsername",
                query = "select dt " +
                        "from DailyTask dt, User u " +
                        "where dt.userId = u.id and u.username = ?1 " +
                        "order by dt.id"
        ),
        NamedQuery(
                name = "DailyTask.findByUserId",
                query = "select dt " +
                        "from DailyTask dt " +
                        "where dt.userId = ?1 " +
                        "order by dt.id"
        )
)
data class DailyTask(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "userid")
        var userId: Long? = null,

        var name: String? = null,

		@Column(name = "start_date")
		var startDate: LocalDate? = null,

		@Enumerated(EnumType.STRING)
		var type: TaskType? = null,

        @Column(name = "intervals", table = "pomodoro_task_intervals")
        var intervals: Int? = null
)