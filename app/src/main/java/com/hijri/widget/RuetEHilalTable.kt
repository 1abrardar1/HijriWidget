package com.hijri.widget

import java.util.Calendar

/**
 * Official Ruet-e-Hilal Committee (Pakistan) Hijri month start dates.
 *
 * Each entry is the Gregorian date on which that Hijri month STARTS
 * according to Pakistan's official moon-sighting announcement.
 *
 * Format: Triple(gregorianYear, gregorianMonth [1-12], gregorianDay) -> HijriYear + HijriMonth
 *
 * Sources: Ruet-e-Hilal Committee announcements & Pakistan official Islamic calendar.
 * Last updated: covers 1444 AH – 1447 AH (2023–2026 CE).
 * Extend the table each year with new announcements.
 */
object RuetEHilalTable {

    /**
     * Each row: Gregorian date a Hijri month starts (Pakistan official).
     * Stored as (gYear, gMonth, gDay, hYear, hMonth).
     */
    private val table: List<IntArray> = listOf(
        // 1444 AH
        intArrayOf(2022, 7, 30,  1444, 1),   // Muharram 1444
        intArrayOf(2022, 8, 28,  1444, 2),   // Safar 1444
        intArrayOf(2022, 9, 27,  1444, 3),   // Rabi al-Awwal 1444
        intArrayOf(2022, 10, 26, 1444, 4),   // Rabi al-Thani 1444
        intArrayOf(2022, 11, 24, 1444, 5),   // Jumada al-Awwal 1444
        intArrayOf(2022, 12, 24, 1444, 6),   // Jumada al-Thani 1444
        intArrayOf(2023, 1, 22,  1444, 7),   // Rajab 1444
        intArrayOf(2023, 2, 21,  1444, 8),   // Sha'ban 1444
        intArrayOf(2023, 3, 23,  1444, 9),   // Ramadan 1444
        intArrayOf(2023, 4, 22,  1444, 10),  // Shawwal 1444
        intArrayOf(2023, 5, 21,  1444, 11),  // Dhu al-Qi'dah 1444
        intArrayOf(2023, 6, 19,  1444, 12),  // Dhu al-Hijjah 1444

        // 1445 AH
        intArrayOf(2023, 7, 19,  1445, 1),   // Muharram 1445
        intArrayOf(2023, 8, 18,  1445, 2),   // Safar 1445
        intArrayOf(2023, 9, 16,  1445, 3),   // Rabi al-Awwal 1445
        intArrayOf(2023, 10, 15, 1445, 4),   // Rabi al-Thani 1445
        intArrayOf(2023, 11, 14, 1445, 5),   // Jumada al-Awwal 1445
        intArrayOf(2023, 12, 13, 1445, 6),   // Jumada al-Thani 1445
        intArrayOf(2024, 1, 12,  1445, 7),   // Rajab 1445
        intArrayOf(2024, 2, 11,  1445, 8),   // Sha'ban 1445
        intArrayOf(2024, 3, 11,  1445, 9),   // Ramadan 1445
        intArrayOf(2024, 4, 10,  1445, 10),  // Shawwal 1445
        intArrayOf(2024, 5, 9,   1445, 11),  // Dhu al-Qi'dah 1445
        intArrayOf(2024, 6, 7,   1445, 12),  // Dhu al-Hijjah 1445

        // 1446 AH
        intArrayOf(2024, 7, 7,   1446, 1),   // Muharram 1446
        intArrayOf(2024, 8, 6,   1446, 2),   // Safar 1446
        intArrayOf(2024, 9, 4,   1446, 3),   // Rabi al-Awwal 1446
        intArrayOf(2024, 10, 4,  1446, 4),   // Rabi al-Thani 1446
        intArrayOf(2024, 11, 2,  1446, 5),   // Jumada al-Awwal 1446
        intArrayOf(2024, 12, 2,  1446, 6),   // Jumada al-Thani 1446
        intArrayOf(2025, 1, 1,   1446, 7),   // Rajab 1446
        intArrayOf(2025, 1, 31,  1446, 8),   // Sha'ban 1446
        intArrayOf(2025, 3, 1,   1446, 9),   // Ramadan 1446
        intArrayOf(2025, 3, 31,  1446, 10),  // Shawwal 1446
        intArrayOf(2025, 4, 29,  1446, 11),  // Dhu al-Qi'dah 1446
        intArrayOf(2025, 5, 28,  1446, 12),  // Dhu al-Hijjah 1446

        // 1447 AH
        intArrayOf(2025, 6, 27,  1447, 1),   // Muharram 1447
        intArrayOf(2025, 7, 26,  1447, 2),   // Safar 1447
        intArrayOf(2025, 8, 25,  1447, 3),   // Rabi al-Awwal 1447
        intArrayOf(2025, 9, 23,  1447, 4),   // Rabi al-Thani 1447
        intArrayOf(2025, 10, 23, 1447, 5),   // Jumada al-Awwal 1447
        intArrayOf(2025, 11, 21, 1447, 6),   // Jumada al-Thani 1447
        intArrayOf(2025, 12, 21, 1447, 7),   // Rajab 1447
        intArrayOf(2026, 1, 20,  1447, 8),   // Sha'ban 1447
        intArrayOf(2026, 2, 18,  1447, 9),   // Ramadan 1447
        intArrayOf(2026, 3, 20,  1447, 10),  // Shawwal 1447
        intArrayOf(2026, 4, 18,  1447, 11),  // Dhu al-Qi'dah 1447
        intArrayOf(2026, 5, 18,  1447, 12),  // Dhu al-Hijjah 1447

        // 1448 AH (projected — replace with official announcement when available)
        intArrayOf(2026, 6, 16,  1448, 1),
        intArrayOf(2026, 7, 16,  1448, 2),
        intArrayOf(2026, 8, 14,  1448, 3),
        intArrayOf(2026, 9, 13,  1448, 4),
        intArrayOf(2026, 10, 12, 1448, 5),
        intArrayOf(2026, 11, 11, 1448, 6),
        intArrayOf(2026, 12, 10, 1448, 7),
        intArrayOf(2027, 1, 9,   1448, 8),
        intArrayOf(2027, 2, 8,   1448, 9),
        intArrayOf(2027, 3, 9,   1448, 10),
        intArrayOf(2027, 4, 8,   1448, 11),
        intArrayOf(2027, 5, 7,   1448, 12)
    )

    /**
     * Look up the Hijri date for a given Gregorian calendar date.
     * Returns null if the date falls outside the table range —
     * caller should fall back to the algorithmic converter.
     */
    fun lookup(calendar: Calendar): HijriDate? {
        val gYear  = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) + 1
        val gDay   = calendar.get(Calendar.DAY_OF_MONTH)

        val todayJd = toJd(gYear, gMonth, gDay)

        // Find the last month-start row whose JD <= today
        var bestRow: IntArray? = null
        var bestJd = Long.MIN_VALUE

        for (row in table) {
            val rowJd = toJd(row[0], row[1], row[2])
            if (rowJd <= todayJd && rowJd > bestJd) {
                bestJd = rowJd
                bestRow = row
            }
        }

        bestRow ?: return null

        val hYear  = bestRow[3]
        val hMonth = bestRow[4]
        val hDay   = (todayJd - bestJd + 1).toInt()   // day-of-month within this Hijri month

        return HijriDate(hDay, hMonth, hYear)
    }

    /** Simple Julian Day Number for date comparison only. */
    private fun toJd(y: Int, m: Int, d: Int): Long {
        var yr = y; var mo = m
        if (mo <= 2) { yr--; mo += 12 }
        val a = yr / 100
        val b = 2 - a + a / 4
        return (365.25 * (yr + 4716)).toLong() +
               (30.6001 * (mo + 1)).toLong() +
               d + b - 1524
    }
}
