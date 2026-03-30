package com.hijri.widget

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.Calendar

object HijriCache {
    private const val PREFS = "hijri_cache"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun todayKey(): String {
        val c = Calendar.getInstance()
        return "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH)+1}-${c.get(Calendar.DAY_OF_MONTH)}"
    }

    fun save(ctx: Context, result: AladhanApiClient.Result) {
        val json = JSONObject().apply {
            put("day",     result.hijriDate.day)
            put("month",   result.hijriDate.month)
            put("year",    result.hijriDate.year)
            put("monthEn", result.hijriMonthNameEn)
            put("monthAr", result.hijriMonthNameAr)
            put("wdEn",    result.weekdayEn)
            put("wdAr",    result.weekdayAr)
            put("source",  result.source.name)
        }
        prefs(ctx).edit().putString(todayKey(), json.toString()).apply()
    }

    fun load(ctx: Context): AladhanApiClient.Result? {
        val raw = prefs(ctx).getString(todayKey(), null) ?: return null
        return try {
            val j = JSONObject(raw)
            val src = try { DateSource.valueOf(j.getString("source")) } catch (e: Exception) { DateSource.API }
            AladhanApiClient.Result(
                hijriDate        = HijriDate(j.getInt("day"), j.getInt("month"), j.getInt("year")),
                hijriMonthNameEn = j.getString("monthEn"),
                hijriMonthNameAr = j.getString("monthAr"),
                weekdayEn        = j.optString("wdEn", ""),
                weekdayAr        = j.optString("wdAr", ""),
                fromApi          = src == DateSource.API,
                source           = src
            )
        } catch (e: Exception) { null }
    }
}
