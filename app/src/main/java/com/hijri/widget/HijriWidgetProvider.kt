package com.hijri.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import kotlinx.coroutines.*
import java.util.Calendar

class HijriWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            showLoading(context, appWidgetManager, id)
            fetchAndUpdate(context, appWidgetManager, id)
        }
    }

    companion object {

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            showLoading(context, appWidgetManager, appWidgetId)
            fetchAndUpdate(context, appWidgetManager, appWidgetId)
        }

        private fun showLoading(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setViewVisibility(R.id.loading_indicator, View.VISIBLE)
            views.setViewVisibility(R.id.content_group, View.INVISIBLE)
            views.setViewVisibility(R.id.tv_source_badge, View.GONE)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun fetchAndUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val now = Calendar.getInstance()

                // 1. Same-day cache
                val cached = HijriCache.load(context)
                if (cached != null) {
                    withContext(Dispatchers.Main) {
                        applyResult(context, appWidgetManager, appWidgetId, cached)
                    }
                    return@launch
                }

                // 2. Live Aladhan API
                val apiResult = if (NetworkHelper.isOnline(context)) {
                    AladhanApiClient.fetchHijriDate(now)
                } else null

                if (apiResult != null) {
                    HijriCache.save(context, apiResult)
                    withContext(Dispatchers.Main) {
                        applyResult(context, appWidgetManager, appWidgetId, apiResult)
                    }
                    return@launch
                }

                // 3. Pakistan Ruet-e-Hilal table
                val pkDate = RuetEHilalTable.lookup(now)
                val fallback = if (pkDate != null) {
                    AladhanApiClient.Result(
                        hijriDate        = pkDate,
                        hijriMonthNameEn = HijriConverter.getMonthName(pkDate.month),
                        hijriMonthNameAr = HijriConverter.getMonthNameAr(pkDate.month),
                        weekdayEn = "", weekdayAr = "",
                        fromApi = false, source = DateSource.PAKISTAN_TABLE
                    )
                } else {
                    val local = HijriConverter.toHijri(now)
                    AladhanApiClient.Result(
                        hijriDate        = local,
                        hijriMonthNameEn = HijriConverter.getMonthName(local.month),
                        hijriMonthNameAr = HijriConverter.getMonthNameAr(local.month),
                        weekdayEn = "", weekdayAr = "",
                        fromApi = false, source = DateSource.ALGORITHM
                    )
                }

                withContext(Dispatchers.Main) {
                    applyResult(context, appWidgetManager, appWidgetId, fallback)
                }
            }
        }

        private fun applyResult(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            result: AladhanApiClient.Result
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            views.setViewVisibility(R.id.loading_indicator, View.GONE)
            views.setViewVisibility(R.id.content_group, View.VISIBLE)

            // Date fields
            views.setTextViewText(R.id.tv_hijri_day,      result.hijriDate.day.toString())
            views.setTextViewText(R.id.tv_hijri_month,     result.hijriMonthNameEn)
            views.setTextViewText(R.id.tv_hijri_month_ar,  result.hijriMonthNameAr)
            views.setTextViewText(R.id.tv_hijri_year,      "${result.hijriDate.year} AH")
            views.setTextViewText(R.id.tv_gregorian_date,  GregorianFormatter.format(Calendar.getInstance()))

            // Moon phase label on widget
            val moonData = MoonPhaseCalculator.calculate(result.hijriDate.day)
            views.setTextViewText(R.id.tv_moon_phase_name, moonData.phase.nameEn.uppercase())

            // Source badge
            val badge = when (result.source) {
                DateSource.API            -> "✓ Aladhan verified"
                DateSource.PAKISTAN_TABLE -> "🌙 Pakistan Ruet-e-Hilal"
                DateSource.ALGORITHM      -> "⚠ Estimate only"
            }
            views.setTextViewText(R.id.tv_source_badge, badge)
            views.setViewVisibility(R.id.tv_source_badge, View.VISIBLE)

            // TAP → open MoonActivity
            val moonIntent = Intent(context, MoonActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val moonPending = PendingIntent.getActivity(
                context, appWidgetId, moonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, moonPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
