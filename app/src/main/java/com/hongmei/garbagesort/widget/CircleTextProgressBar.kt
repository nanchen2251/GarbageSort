package com.hongmei.garbagesort.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.hongmei.garbagesort.R
import kotlin.math.abs
import kotlin.math.min

/**
 *
 * 中间带文本的进度条，进度条为圆角
 */
class CircleTextProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 10 // 半径
    private var arcColorBg = Color.BLACK
    private var arcColor = Color.RED
    private var currentProgress = 0

    private var arcWidth = 10
    private var text = ""
    private val maxProgress = 100
    private var maxDuration = 70
    private var textColor = Color.WHITE
    private var textSize = 12
    private var isFakeBoldText = false

    private val arcPaintBg by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val arcPaint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val textPaint by lazy(LazyThreadSafetyMode.NONE) { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val arcRectF by lazy(LazyThreadSafetyMode.NONE) { RectF() }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextProgressBar)
        arcColorBg =
            typedArray.getColor(R.styleable.CircleTextProgressBar_ctp_arcColorBg, 0x1600ac2b)
        arcColor = typedArray.getColor(R.styleable.CircleTextProgressBar_ctp_arcColor, 0x00d087)
        arcWidth =
            typedArray.getDimensionPixelSize(R.styleable.CircleTextProgressBar_ctp_arcWidth, 30)
        radius =
            typedArray.getDimensionPixelSize(R.styleable.CircleTextProgressBar_ctp_circleRadius, 60)
        currentProgress = typedArray.getInteger(R.styleable.CircleTextProgressBar_ctp_progress, 0)
        text = typedArray.getString(R.styleable.CircleTextProgressBar_ctp_centerText) ?: ""
        textColor = typedArray.getColor(R.styleable.CircleTextProgressBar_ctp_centerTextColor, Color.WHITE)
        textSize = typedArray.getDimensionPixelSize(R.styleable.CircleTextProgressBar_ctp_centerTextSize, 36)
        isFakeBoldText = typedArray.getBoolean(R.styleable.CircleTextProgressBar_ctp_is_fake_bold_text, false)

        typedArray.recycle()

        arcPaintBg.style = Paint.Style.STROKE
        arcPaintBg.strokeWidth = arcWidth.toFloat()
        arcPaintBg.color = arcColorBg

        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeWidth = arcWidth.toFloat()
        arcPaint.color = arcColor
        arcPaint.strokeCap = Paint.Cap.ROUND

        textPaint.style = Paint.Style.FILL
        textPaint.color = textColor
        textPaint.textSize = textSize.toFloat()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isFakeBoldText = isFakeBoldText
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getMeasureDimension(widthMeasureSpec),
            getMeasureDimension(heightMeasureSpec)
        )
    }

    private fun getMeasureDimension(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> min(2 * radius, specSize)
            else -> 2 * radius
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        canvas.drawCircle(width / 2f, height / 2f, radius - arcWidth / 2f, arcPaintBg)

        arcRectF.set(
            width / 2f - radius + arcWidth / 2f,
            height / 2f - radius + arcWidth / 2f,
            width / 2f + radius - arcWidth / 2f,
            height / 2f + radius - arcWidth / 2f
        )
        canvas.drawArc(arcRectF, 270f, 360 * currentProgress / 100f, false, arcPaint)

        val fontMetrics = textPaint.fontMetrics
        val offset = (fontMetrics.descent - fontMetrics.ascent) / 2f - fontMetrics.descent
        val x = width / 2f
        val y = height / 2f + offset
        // 左下角偏上坐标
        canvas.drawText(text, x, y, textPaint)
    }

    fun setProgressWithAnim(progress: Int, maxDuration: Int) {
        if (progress < 0) return
        val animator = if (progress > maxProgress) {
            ObjectAnimator.ofInt(progress, maxProgress)
        } else {
            ObjectAnimator.ofInt(currentProgress, progress)
        }
        animator.duration = (abs(currentProgress - progress) * maxDuration).toLong()
        animator.addUpdateListener {
            currentProgress = it.animatedValue as Int
            invalidate()
        }
        animator.start()
    }

    fun updatePercent(progress: Int, maxProgress: Int) {
        this.currentProgress = if (maxProgress <= 0) {
            this.maxProgress
        } else {
            progress * 100 / maxProgress
        }
        invalidate()
    }

    fun setCenterText(text: String) {
        this.text = text
    }
}