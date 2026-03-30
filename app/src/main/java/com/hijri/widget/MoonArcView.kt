package com.hijri.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.*

class MoonArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var moonData: MoonPhaseCalculator.MoonData? = null
        set(value) {
            field = value
            startAnimation()
        }

    private var animatedProgress = 0f
    private var animator: ValueAnimator? = null

    private val skyPaint = Paint()

    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(60, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(12f, 8f), 0f)
    }

    private val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val moonShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    private val subLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 255, 255, 255)
        textAlign = Paint.Align.CENTER
    }

    private val horizonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(40, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val stars = mutableListOf<FloatArray>() // [x_frac, y_frac, radius, alpha]

    init {
        repeat(80) {
            stars.add(
                floatArrayOf(
                    Math.random().toFloat(),
                    Math.random().toFloat() * 0.75f,
                    (0.5f + Math.random().toFloat() * 1.5f),
                    (100 + Math.random().toFloat() * 155f)
                )
            )
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun startAnimation() {
        animator?.cancel()
        val target = moonData?.arcProgress ?: 0f
        animator = ValueAnimator.ofFloat(0f, target).apply {
            duration = 1800
            interpolator = DecelerateInterpolator(1.5f)
            addUpdateListener {
                animatedProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val data = moonData ?: return

        drawSky(canvas, w, h)
        drawStars(canvas, w, h)
        drawHorizon(canvas, w, h)
        drawArcPath(canvas, w, h)
        drawMoon(canvas, w, h, data)
        drawLabels(canvas, w, h, data)
        drawIlluminationBar(canvas, w, h, data)
    }

    private fun drawSky(canvas: Canvas, w: Float, h: Float) {
        val shader = LinearGradient(
            0f, 0f, 0f, h,
            intArrayOf(
                Color.parseColor("#050d1a"),
                Color.parseColor("#0a1628"),
                Color.parseColor("#111f3a"),
                Color.parseColor("#1a2a4a")
            ),
            floatArrayOf(0f, 0.4f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        skyPaint.shader = shader
        canvas.drawRect(0f, 0f, w, h, skyPaint)
    }

    private fun drawStars(canvas: Canvas, w: Float, h: Float) {
        val moonPos = getMoonPosition(w, h)
        for (star in stars) {
            val sx = star[0] * w
            val sy = star[1] * h
            val dist = hypot(sx - moonPos.x, sy - moonPos.y)
            val fade = (dist / 120f).coerceIn(0f, 1f)
            val twinkle = (sin(System.currentTimeMillis() * 0.002 + sx.toDouble()) * 0.3 + 0.7).toFloat()
            starPaint.alpha = (star[3] * fade * twinkle).toInt().coerceIn(0, 255)
            canvas.drawCircle(sx, sy, star[2], starPaint)
        }
    }

    private fun drawHorizon(canvas: Canvas, w: Float, h: Float) {
        val horizonY = h * 0.82f
        canvas.drawLine(0f, horizonY, w, horizonY, horizonPaint)

        val groundShader = LinearGradient(
            0f, horizonY, 0f, h,
            intArrayOf(Color.argb(30, 100, 150, 255), Color.TRANSPARENT),
            null,
            Shader.TileMode.CLAMP
        )
        val gp = Paint().apply { shader = groundShader }
        canvas.drawRect(0f, horizonY, w, h, gp)
    }

    private fun getArcControlPoint(w: Float, h: Float): PointF {
        val horizonY = h * 0.82f
        return PointF(w / 2f, horizonY - h * 0.70f)
    }

    private fun getMoonPosition(w: Float, h: Float): PointF {
        val horizonY = h * 0.82f
        val startX = w * 0.08f
        val endX = w * 0.92f
        val ctrl = getArcControlPoint(w, h)
        val t = animatedProgress

        val x = (1 - t) * (1 - t) * startX + 2f * (1 - t) * t * ctrl.x + t * t * endX
        val y = (1 - t) * (1 - t) * horizonY + 2f * (1 - t) * t * ctrl.y + t * t * horizonY
        return PointF(x, y)
    }

    private fun drawArcPath(canvas: Canvas, w: Float, h: Float) {
        val horizonY = h * 0.82f
        val startX = w * 0.08f
        val endX = w * 0.92f
        val ctrl = getArcControlPoint(w, h)

        val path = Path().apply {
            moveTo(startX, horizonY)
            quadTo(ctrl.x, ctrl.y, endX, horizonY)
        }
        canvas.drawPath(path, arcPaint)

        val ticks = listOf(0.25f, 0.5f, 0.75f)
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(80, 255, 255, 255)
            strokeWidth = 1.5f
        }

        for (t in ticks) {
            val x = (1 - t) * (1 - t) * startX + 2f * (1 - t) * t * ctrl.x + t * t * endX
            val y = (1 - t) * (1 - t) * horizonY + 2f * (1 - t) * t * ctrl.y + t * t * horizonY
            canvas.drawCircle(x, y, 3f, tickPaint)
        }
    }

    private fun drawMoon(canvas: Canvas, w: Float, h: Float, data: MoonPhaseCalculator.MoonData) {
        val pos = getMoonPosition(w, h)
        val radius = w * 0.085f
        val illum = data.illumination

        val glowRadius = radius * (1.6f + illum * 0.8f)
        val glowAlpha = (30 + illum * 60).toInt()
        glowPaint.shader = RadialGradient(
            pos.x, pos.y, glowRadius,
            intArrayOf(
                Color.argb(glowAlpha, 255, 240, 180),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(pos.x, pos.y, glowRadius, glowPaint)

        val bounds = RectF(
            pos.x - radius * 2f,
            pos.y - radius * 2f,
            pos.x + radius * 2f,
            pos.y + radius * 2f
        )
        canvas.saveLayer(bounds, null)

        moonPaint.color = Color.argb(255, 245, 235, 200)
        canvas.drawCircle(pos.x, pos.y, radius, moonPaint)

        val shadowOffset = radius * (1f - illum * 2f).coerceIn(-1f, 1f)
        val shadowX = if (data.isWaxing) pos.x - shadowOffset else pos.x + shadowOffset
        moonShadowPaint.color = Color.argb(255, 10, 18, 35)
        canvas.drawCircle(shadowX, pos.y, radius * 1.01f, moonShadowPaint)

        canvas.restore()

        if (illum > 0.3f) {
            val craterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb((illum * 25).toInt(), 160, 145, 110)
                style = Paint.Style.FILL
            }
            val craters = listOf(
                floatArrayOf(-0.3f, -0.2f, 0.12f),
                floatArrayOf(0.2f, 0.3f, 0.09f),
                floatArrayOf(-0.1f, 0.35f, 0.07f),
                floatArrayOf(0.35f, -0.1f, 0.08f),
            )
            for (c in craters) {
                canvas.drawCircle(pos.x + c[0] * radius, pos.y + c[1] * radius, c[2] * radius, craterPaint)
            }
        }
    }

    private fun drawLabels(canvas: Canvas, w: Float, h: Float, data: MoonPhaseCalculator.MoonData) {
        val horizonY = h * 0.82f
        val centerX = w / 2f

        val phaseName = data.phase.nameEn.uppercase()
        labelPaint.textSize = w * 0.044f
        labelPaint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)

        val pillPaddingX = w * 0.055f
        val pillH = w * 0.08f
        val pillW = labelPaint.measureText(phaseName) + pillPaddingX * 2f
        val pillTop = horizonY + h * 0.028f
        val pillRect = RectF(
            centerX - pillW / 2f,
            pillTop,
            centerX + pillW / 2f,
            pillTop + pillH
        )

        val pillFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(65, 255, 255, 255)
            style = Paint.Style.FILL
        }

        val pillStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(55, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, pillFillPaint)
        canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, pillStrokePaint)
        canvas.drawText(phaseName, centerX, pillTop + pillH * 0.66f, labelPaint)

        val arabicPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(245, 255, 245, 220)
            textAlign = Paint.Align.CENTER
            textSize = w * 0.043f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            setShadowLayer(19f, 0f, 0f, Color.argb(100, 255, 215, 120))
        }

        canvas.drawText(
            data.phase.nameAr,
            centerX,
            pillTop + pillH + w * 0.055f,
            arabicPaint
        )

        subLabelPaint.textSize = w * 0.032f
        subLabelPaint.color = Color.argb(190, 255, 255, 255)
        canvas.drawText(
            "Day ${data.hijriDay} of 30",
            centerX,
            h * 0.06f,
            subLabelPaint
        )
    }

    private fun drawIlluminationBar(canvas: Canvas, w: Float, h: Float, data: MoonPhaseCalculator.MoonData) {
        val barW = w * 0.58f
        val barH = h * 0.020f
        val left = (w - barW) / 2f
        val top = h * 0.895f
        val radius = barH / 2f

        val trackRect = RectF(left, top, left + barW, top + barH)

        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(55, 255, 255, 255)
            style = Paint.Style.FILL
        }

        val trackStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(40, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                left, top, left + barW, top,
                Color.argb(235, 255, 210, 90),
                Color.argb(255, 255, 230, 140),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }

        canvas.drawRoundRect(trackRect, radius, radius, trackPaint)
        canvas.drawRoundRect(trackRect, radius, radius, trackStrokePaint)

        val fillW = barW * animatedProgress
        if (fillW > radius * 2f) {
            val fillRect = RectF(left, top, left + fillW, top + barH)
            canvas.drawRoundRect(fillRect, radius, radius, fillPaint)
        }

        subLabelPaint.textSize = h * 0.022f
        subLabelPaint.color = Color.argb(205, 230, 238, 247)
        canvas.drawText(
            "${(data.illumination * 100).toInt()}% illuminated",
            w / 2f,
            top + barH + h * 0.034f,
            subLabelPaint
        )
    }
}
