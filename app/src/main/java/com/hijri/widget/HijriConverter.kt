package com.hijri.widget

import java.util.Calendar

data class HijriDate(val day: Int, val month: Int, val year: Int)

object HijriConverter {

    private val HIJRI_MONTH_NAMES = arrayOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val HIJRI_MONTH_NAMES_AR = arrayOf(
        "مُحَرَّم", "صَفَر", "رَبيع الأوَّل", "رَبيع الثاني",
        "جُمادى الأولى", "جُمادى الآخرة", "رَجَب", "شَعبان",
        "رَمَضان", "شَوَّال", "ذو القَعدة", "ذو الحِجَّة"
    )

    fun toHijri(calendar: Calendar): HijriDate {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        // Julian Day Number algorithm
        val jd = gregorianToJulian(day, month, year)
        return julianToHijri(jd)
    }

    private fun gregorianToJulian(day: Int, month: Int, year: Int): Long {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = (y / 100).toLong()
        val b = 2 - a + (a / 4)
        return (365.25 * (y + 4716)).toLong() +
                (30.6001 * (m + 1)).toLong() +
                day + b - 1524
    }

    private fun julianToHijri(jd: Long): HijriDate {
        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316).toInt() * ((50 * l2) / 17719).toInt() +
                (l2 / 5670).toInt() * ((43 * l2) / 15238).toInt()
        val l3 = l2 - ((30 - j) / 15).toInt() * ((17719 * j) / 50).toInt() -
                (j / 16).toInt() * ((15238 * j) / 43).toInt() + 29
        val month = ((24 * l3) / 709).toInt()
        val day = l3 - ((709 * month) / 24).toInt()
        val year = 30 * n + j - 30

        return HijriDate(day, month, year)
    }

    fun getMonthName(month: Int): String {
        return if (month in 1..12) HIJRI_MONTH_NAMES[month - 1] else "Unknown"
    }

    fun getMonthNameAr(month: Int): String {
        return if (month in 1..12) HIJRI_MONTH_NAMES_AR[month - 1] else ""
    }
}
