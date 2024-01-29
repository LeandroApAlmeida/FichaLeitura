package br.com.leandro.fichaleitura.utils

import java.util.Calendar
import java.util.Date


fun getSystemTime(): Long = Date().time


fun getFirstDayOfMonth(): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
    return calendar.time
}


fun getLastDayOfMonth(): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))
    return calendar.time
}


fun getFirstDayOfYear(): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
    return calendar.time
}


fun getLastDayOfYear(): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))
    return calendar.time
}


fun getDayOfMonth(date: Date): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.DAY_OF_MONTH)
}


fun getMonthOfYear(date: Date): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.MONTH) + 1
}


fun getYearOfDate(date: Date): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.YEAR)
}


fun getDateBy(year: Int, month: Int, day: Int): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.time
}