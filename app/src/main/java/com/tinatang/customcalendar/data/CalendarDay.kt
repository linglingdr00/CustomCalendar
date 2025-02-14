package com.tinatang.customcalendar.data

import java.time.LocalDate

data class CalendarDay(
    val position: Int,
    val date: LocalDate,
    val enable: Boolean
)