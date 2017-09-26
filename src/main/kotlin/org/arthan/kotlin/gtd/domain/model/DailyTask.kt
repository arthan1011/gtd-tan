package org.arthan.kotlin.gtd.domain.model

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
@NamedQuery(
        name = "DailyTask.findByUsername",
        query = "select dt " +
                "from DailyTask dt, User u " +
                "where dt.userId = u.id and u.username = ?1 " +
				"order by dt.startDate"
)
data class DailyTask(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "userid")
        var userId: Long? = null,

        var name: String? = null,

		@Column(name = "start_date")
		var startDate: LocalDate? = null
)