package com.hijri.widget

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import java.util.Calendar

class MoonActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Full screen, no status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_moon)

        val moonArcView = findViewById<MoonArcView>(R.id.moon_arc_view)
        val tvHijriDate = findViewById<TextView>(R.id.tv_hijri_full)
        val tvGreg      = findViewById<TextView>(R.id.tv_greg_full)
        val btnClose    = findViewById<View>(R.id.btn_close)

        // Get Hijri date — prefer cached API result, fall back to local
        val now   = Calendar.getInstance()
        val hijri = HijriCache.load(this)?.hijriDate
            ?: RuetEHilalTable.lookup(now)
            ?: HijriConverter.toHijri(now)

        val monthEn = HijriConverter.getMonthName(hijri.month)
        val monthAr = HijriConverter.getMonthNameAr(hijri.month)

        tvHijriDate.text = "${hijri.day} $monthEn ${hijri.year} AH\n$monthAr"
        tvGreg.text      = GregorianFormatter.format(now)

        // Calculate moon phase and hand to the view
        moonArcView.moonData = MoonPhaseCalculator.calculate(hijri.day)

        // Close on tap anywhere or on X button
        btnClose.setOnClickListener { finish() }
        moonArcView.setOnClickListener { finish() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
        }
    }
}
