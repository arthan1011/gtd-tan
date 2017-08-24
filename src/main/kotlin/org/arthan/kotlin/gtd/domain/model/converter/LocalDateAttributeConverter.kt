package org.arthan.kotlin.gtd.domain.model.converter

import java.sql.Date
import java.time.LocalDate
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Converter LocalDate to/from Date
 * Created by shamsiev on 24.08.2017 for gtd-tan.
 */

@Converter(autoApply = true)
class LocalDateAttributeConverter : AttributeConverter<LocalDate, Date> {
	override fun convertToEntityAttribute(dbData: Date?): LocalDate? {
		return dbData?.toLocalDate()
	}

	override fun convertToDatabaseColumn(attribute: LocalDate?): Date? {
		return attribute?.let { Date.valueOf(attribute) }
	}
}