package com.hijri.widget

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

object AladhanApiClient {

    private const val TAG = "AladhanApi"
    private const val BASE_URL = "https://api.aladhan.com/v1/gToH"
    private const val TIMEOUT_MS = 8000

    data class Result(
        val hijriDate: HijriDate,
        val hijriMonthNameEn: String,
        val hijriMonthNameAr: String,
        val weekdayEn: String,
        val weekdayAr: String,
        val fromApi: Boolean,
        val source: DateSource = if (fromApi) DateSource.API else DateSource.ALGORITHM
    )

    fun fetchHijriDate(calendar: Calendar): Result? {
        val day   = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year  = calendar.get(Calendar.YEAR)
        val urlString = "$BASE_URL?date=$day-$month-$year"

        return try {
            val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout    = TIMEOUT_MS
                requestMethod  = "GET"
            }
            if (conn.responseCode != 200) { Log.w(TAG, "Non-200: ${conn.responseCode}"); return null }
            val body = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            parseResponse(body)
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed: ${e.message}")
            null
        }
    }

    private fun parseResponse(json: String): Result? {
        return try {
            val hijri    = JSONObject(json).getJSONObject("data").getJSONObject("hijri")
            val weekday  = hijri.getJSONObject("weekday")
            val monthObj = hijri.getJSONObject("month")
            Result(
                hijriDate        = HijriDate(hijri.getString("day").toInt(), monthObj.getInt("number"), hijri.getString("year").toInt()),
                hijriMonthNameEn = monthObj.getString("en"),
                hijriMonthNameAr = monthObj.getString("ar"),
                weekdayEn        = weekday.getString("en"),
                weekdayAr        = weekday.getString("ar"),
                fromApi          = true,
                source           = DateSource.API
            )
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}")
            null
        }
    }
}
