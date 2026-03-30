package com.hijri.widget

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object GregorianFormatter {
    private val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH)

    fun format(calendar: Calendar): String = formatter.format(calendar.time)
}
