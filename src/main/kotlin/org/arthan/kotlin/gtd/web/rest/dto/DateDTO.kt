package org.arthan.kotlin.gtd.web.rest.dto

// todo: Использовать Int вместо String и убрать значения по умолчанию
data class DateDTO(var day: String = "",
                   var month: String = "",
                   var year: String = "")