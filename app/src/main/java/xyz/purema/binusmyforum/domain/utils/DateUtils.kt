package xyz.purema.binusmyforum.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    private val dateFormat =
        DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.forLanguageTag("id-ID"))

    fun formatDate(date: LocalDate): String = date.format(dateFormat)
}