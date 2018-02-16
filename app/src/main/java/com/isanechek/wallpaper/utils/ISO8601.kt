package com.isanechek.wallpaper.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

object ISO8601 {

    private val FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    fun fromCalendar(calendar: Calendar): String {
        val date = calendar.time
        val formatted = SimpleDateFormat(FORMAT).format(date)
        return formatted.substring(0, 22) + ":" + formatted.substring(22)
    }

    @Throws(ParseException::class)
    fun toCalendar(iso8601string: String): Calendar {
        val calendar = GregorianCalendar.getInstance()
        var s = iso8601string.replace("Z", "+00:00")
        try {
            s = s.substring(0, 22) + s.substring(23)
        } catch (e: IndexOutOfBoundsException) {
            throw ParseException("Invalid length", 0)
        }

        val date = SimpleDateFormat(FORMAT).parse(s)
        calendar.time = date
        return calendar
    }
}
/**
 * потому что в обьект нельзя передать статический контекст
 */
fun parse(iso8601string: String): Date? {
    try {
        return ISO8601.toCalendar(iso8601string).time
    } catch (e: ParseException) {
        return null
    }

}