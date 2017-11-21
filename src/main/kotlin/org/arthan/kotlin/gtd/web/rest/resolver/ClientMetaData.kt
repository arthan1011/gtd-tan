package org.arthan.kotlin.gtd.web.rest.resolver

/**
 * for extra info in client requests
 * Created by shamsiev on 25.08.2017 for gtd-tan.
 */

val TIME_OFFSET_HEADER = "AX-GTD-Minute-Offset"

data class ClientMetaData(val minuteOffset: Int) {
	constructor() : this(0)
}