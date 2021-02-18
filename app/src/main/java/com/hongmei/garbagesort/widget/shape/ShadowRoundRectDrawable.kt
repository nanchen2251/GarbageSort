package com.hongmei.garbagesort.widget.shape

import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import com.hongmei.garbagesort.ext.toPxF

fun generateRadius(value: Float = 12.toPxF()): FloatArray {
    return floatArrayOf(
        value, value, value, value,
        value, value, value, value
    )
}

fun View.updateShadowBackground(params: ShadowRoundRectDrawable.Params) {
    val background = this.background

    if (background == null || background !is LayerDrawable) {
        // 如果当前的背景不是阴影背景，需要更新到最新的阴影背景
        wrapDrawableToView(this, params)
    } else {
        try {
            val drawable = background.getDrawable(0) as? ShadowRoundRectDrawable
            // 如果属性不一致，才需要更新当前的阴影
            if (drawable?.params != params) {
                wrapDrawableToView(this, params)
            }
        } catch (t: Throwable) {
            wrapDrawableToView(this, params)
        }
    }
}

fun colorWithOpacity(@ColorInt color: Int, alpha: Float): Int {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    return Color.argb((alpha * 255).toInt(), r, g, b)
}

private fun wrapDrawableToView(view: View, params: ShadowRoundRectDrawable.Params) {
    val shadowDrawable = ShadowRoundRectDrawable(params)
    view.background = shadowDrawable.wrapLayerDrawable()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && shadowDrawable.paint != null) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, shadowDrawable.paint)
    }
}

class ShadowRoundRectDrawable constructor(val params: Params) : ShapeDrawable() {
    var desiredLeftPadding = 0F
    var desiredRightPadding = 0F
    var desiredTopPadding = 0F
    var desiredBottomPadding = 0F
    private var realDx = 0F
    private var realDy = 0F
    private var correctShadowRadius = 0F

    init {
        applyParam(params)
    }

    fun wrapLayerDrawable(): LayerDrawable {
        val drawable = LayerDrawable(arrayOf(this))
        drawable.setLayerInset(
            0,
            desiredLeftPadding.toInt(),
            desiredTopPadding.toInt(),
            desiredRightPadding.toInt(),
            desiredBottomPadding.toInt()
        )
        return drawable
    }

    fun applyLayerDrawableInset(index: Int, layerDrawable: LayerDrawable) {
        layerDrawable.setLayerInset(
            index,
            desiredLeftPadding.toInt(),
            desiredTopPadding.toInt(),
            desiredRightPadding.toInt(),
            desiredBottomPadding.toInt()
        )
    }

    fun applyParam(params: Params) {
        if (params.outerRadii.isEmpty()) return

        val shadowRadius = params.shadowRadius
        correctShadowRadius = shadowRadius.toFloat()
        val dx = params.dx.toFloat()
        val dy = params.dy.toFloat()

        val alpha = Color.alpha(params.shadowColor)
        val alphaToFloat = alpha.toFloat() / 255

        // 先假定dx、dy都为0；然后按照 Figma 的参数，先算出 UI 同学期望的阴影 padding 值。
        desiredLeftPadding = shadowRadius.toFloat()
        desiredRightPadding = shadowRadius.toFloat()
        desiredTopPadding = shadowRadius.toFloat()
        desiredBottomPadding = shadowRadius.toFloat()
        if (params.excludeDx) {
            desiredLeftPadding = 0F
            desiredRightPadding = 0F
        }

        realDx = dx
        realDy = dy

        if (params.dx != 0 && !params.excludeDx) {
            desiredLeftPadding -= dx
            desiredRightPadding += dx

            val outerShadowDx = alphaToFloat * dx
            correctShadowRadius = shadowRadius - (outerShadowDx / 2)
            realDx = dx - outerShadowDx + (outerShadowDx / 2)
        }

        if (params.dy != 0) {
            desiredTopPadding -= dy
            desiredBottomPadding += dy

            val outerShadowDy = alphaToFloat * dy
            correctShadowRadius = shadowRadius - (outerShadowDy / 2)
            realDy = dy - outerShadowDy + (outerShadowDy / 2)
        }

        paint.isAntiAlias = true
        paint.color = params.shapeColor
        paint.setShadowLayer(
            correctShadowRadius,
            realDx,
            realDy,
            params.shadowColor
        )
        shape = RoundRectShape(params.outerRadii, null, null)
    }

    class Params(var outerRadii: FloatArray,
                 @ColorInt var shapeColor: Int = 0,
                 var dx: Int = 0,
                 var dy: Int = 0,
                 var shadowRadius: Int = 0,
                 var excludeDx: Boolean = false,
                 @ColorInt var shadowColor: Int = 0) {
        override fun hashCode(): Int {
            return dx + dy + ((shadowColor.toLong() + shadowRadius + shapeColor) / 3).toInt()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Params) return false

            return other.shapeColor == shapeColor
                    && other.shadowRadius == shadowRadius
                    && other.dx == dx
                    && other.dy == dy
                    && other.shadowColor == shadowColor
                    && other.excludeDx == excludeDx
                    && other.outerRadii contentEquals outerRadii
        }
    }
}