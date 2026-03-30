package com.hijri.widget

import kotlin.math.PI
import kotlin.math.cos

/**
 * Calculates moon phase data from the Hijri day-of-month.
 * The Islamic calendar is purely lunar, so Hijri day 1 = new moon,
 * day 14-15 = full moon, day 29/30 = waning crescent back to new.
 */
object MoonPhaseCalculator {

    enum class Phase(val nameEn: String, val nameAr: String, val emoji: String) {
        NEW_MOON        ("New Moon",          "المحاق",          "🌑"),
        WAXING_CRESCENT ("Waxing Crescent",   "الهلال المتزايد", "🌒"),
        FIRST_QUARTER   ("First Quarter",     "التربيع الأول",   "🌓"),
        WAXING_GIBBOUS  ("Waxing Gibbous",    "الأحدب المتزايد", "🌔"),
        FULL_MOON       ("Full Moon",         "البدر",           "🌕"),
        WANING_GIBBOUS  ("Waning Gibbous",    "الأحدب المتناقص", "🌖"),
        LAST_QUARTER    ("Last Quarter",      "التربيع الأخير",  "🌗"),
        WANING_CRESCENT ("Waning Crescent",   "الهلال المتناقص", "🌘")
    }

    data class MoonData(
        val phase: Phase,
        val hijriDay: Int,
        /** 0.0 = new moon, 1.0 = full moon, back to 0.0 */
        val illumination: Float,
        /** 0.0 (just risen) → 1.0 (about to set) — position along the arc */
        val arcProgress: Float,
        /** true = crescent opens to the right (waxing), false = left (waning) */
        val isWaxing: Boolean
    )

    fun calculate(hijriDay: Int): MoonData {
        val day = hijriDay.coerceIn(1, 30)

        // Illumination: 0 at new moon (day 1), peaks at full moon (day 15), back to 0 at day 30
        val cycleAngle = (day - 1).toFloat() / 29.5f * 2 * PI.toFloat()
        val illumination = ((1f - cos(cycleAngle)) / 2f).coerceIn(0f, 1f)

        // Arc progress: moon rises higher as the month progresses, peaks at full moon
        // Use same cycle so arc position mirrors illumination
        val arcProgress = illumination

        val isWaxing = day <= 15

        val phase = when (day) {
            1           -> Phase.NEW_MOON
            in 2..6     -> Phase.WAXING_CRESCENT
            in 7..9     -> Phase.FIRST_QUARTER
            in 10..14   -> Phase.WAXING_GIBBOUS
            in 15..16   -> Phase.FULL_MOON
            in 17..21   -> Phase.WANING_GIBBOUS
            in 22..24   -> Phase.LAST_QUARTER
            else        -> Phase.WANING_CRESCENT
        }

        return MoonData(phase, day, illumination, arcProgress, isWaxing)
    }
}
