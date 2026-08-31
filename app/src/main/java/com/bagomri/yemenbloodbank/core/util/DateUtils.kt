package com.bagomri.yemenbloodbank.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * وظائف مساعدة لمعالجة وتنسيق التواريخ بالعربية
 */
object DateUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val displayFormat = SimpleDateFormat("yyyy/MM/dd", Locale.forLanguageTag("ar"))
    private val displayWithTimeFormat = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.forLanguageTag("ar"))

    fun parseIsoDate(isoString: String?): Date? {
        if (isoString.isNullOrEmpty()) return null
        return try {
            val cleaned = isoString.substringBefore(".").substringBefore("+").substringBefore("Z")
            isoFormat.parse(cleaned)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: Date?): String {
        if (date == null) return "-"
        return displayFormat.format(date)
    }

    fun formatDate(isoString: String?): String {
        val date = parseIsoDate(isoString) ?: return isoString ?: "-"
        return displayFormat.format(date)
    }

    fun formatIsoToDisplay(isoString: String?): String = formatDate(isoString)

    fun formatDateTime(date: Date?): String {
        if (date == null) return "-"
        return displayWithTimeFormat.format(date)
    }

    fun formatDateTime(isoString: String?): String {
        val date = parseIsoDate(isoString) ?: return isoString ?: "-"
        return displayWithTimeFormat.format(date)
    }

    fun getDaysDifference(targetDate: Date, fromDate: Date = Date()): Long {
        val diffMillis = targetDate.time - fromDate.time
        return diffMillis / (1000 * 60 * 60 * 24)
    }

    fun isDateAfter(dateToCheck: Date?, compareDate: Date = Date()): Boolean {
        if (dateToCheck == null) return false
        return dateToCheck.after(compareDate)
    }
}
