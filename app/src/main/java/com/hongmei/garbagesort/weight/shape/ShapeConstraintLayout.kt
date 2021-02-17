package com.hongmei.garbagesort.weight.shape

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.weight.shape.ShapeConst.BOTTOM_LEFT
import com.hongmei.garbagesort.weight.shape.ShapeConst.BOTTOM_RIGHT
import com.hongmei.garbagesort.weight.shape.ShapeConst.TOP_LEFT
import com.hongmei.garbagesort.weight.shape.ShapeConst.TOP_RIGHT

/**
 * 自定义圆角的 ConstraintLayout - 支持阴影
 *
 * Author: nanchen
 * Email: liushilin.nanchen@bytedance.com
 * Date: 2019-10-14 11:38
 */
open class ShapeConstraintLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    /**
     * shape模式
     * 矩形（rectangle）、椭圆形(oval)、线形(line)、环形(ring)
     */
    private var shapeMode = 0

    /**
     * 填充颜色
     */
    protected var fillColor = 0

    /**
     * 描边颜色
     */
    private var strokeColor = 0

    /**
     * 描边宽度
     */
    private var strokeWidth = 0

    /**
     * 圆角半径
     */
    protected var cornerRadius = 0

    /**
     * 圆角位置
     * topLeft、topRight、bottomRight、bottomLeft
     */
    private var cornerPosition = -1

    /**
     * 起始颜色
     */
    private var startColor = 0

    /**
     * 结束颜色
     */
    private var endColor = 0

    /**
     * 渐变方向
     * 0-GradientDrawable.Orientation.TOP_BOTTOM
     * 1-GradientDrawable.Orientation.TR_BL
     * 2-GradientDrawable.Orientation.RIGHT_LEFT
     * 3-GradientDrawable.Orientation.BR_TL
     * 4-GradientDrawable.Orientation.BOTTOM_TOP
     * 5-GradientDrawable.Orientation.BL_TR
     * 6-GradientDrawable.Orientation.LEFT_RIGHT
     * 7-GradientDrawable.Orientation.TL_BR
     */
    protected var orientation = 0

    /**
     * 阴影效果
     */
    private var withElevation = 0

    private var shadowDx = 0
    private var shadowDy = 0
    private var shadowBlurRadius = 0
    private var shadowRadii: FloatArray? = null

    @ColorInt
    private var shadowColor: Int = Color.TRANSPARENT

    private var excludeDx = false
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * shape样式
     */
    private val gradientDrawable: GradientDrawable by lazy(LazyThreadSafetyMode.NONE) { GradientDrawable() }

    private val shadowRoundRectDrawable: ShadowRoundRectDrawable by lazy(LazyThreadSafetyMode.NONE) {
        val params = ShadowRoundRectDrawable.Params(floatArrayOf())
        copyLocalParamsToShadow(params)
        ShadowRoundRectDrawable(params)
    }

    private val shadowLayerDrawable: LayerDrawable by lazy(LazyThreadSafetyMode.NONE) {
        LayerDrawable(arrayOf(shadowRoundRectDrawable, gradientDrawable))
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ShapeConstraintLayout).apply {
            shapeMode = getInt(R.styleable.ShapeConstraintLayout_sc_shapeMode, 0)
            fillColor = getColor(R.styleable.ShapeConstraintLayout_sc_fillColor, 0xFFFFFFFF.toInt())
            strokeColor = getColor(R.styleable.ShapeConstraintLayout_sc_strokeColor, 0)
            strokeWidth = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_strokeWidth, 0)
            cornerRadius = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_cornerRadius, 0)
            cornerPosition = getInt(R.styleable.ShapeConstraintLayout_sc_cornerPosition, -1)
            startColor = getColor(R.styleable.ShapeConstraintLayout_sc_startColor, 0xFFFFFFFF.toInt())
            endColor = getColor(R.styleable.ShapeConstraintLayout_sc_endColor, 0xFFFFFFFF.toInt())
            orientation = getColor(R.styleable.ShapeConstraintLayout_sc_orientation, 0)
            withElevation = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_withElevation, 0)
            shadowColor = getColor(R.styleable.ShapeConstraintLayout_sc_shadow_color, Color.TRANSPARENT)
            excludeDx = getBoolean(R.styleable.ShapeConstraintLayout_sc_exclude_dx, false)
            shadowDx = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_shadow_dx, 0)
            shadowDy = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_shadow_dy, 0)
            shadowBlurRadius = getDimensionPixelSize(R.styleable.ShapeConstraintLayout_sc_shadow_blur_radius, 0)
            recycle()
        }
    }

    /**
     * 在代码中修改属性
     * 由于在代码中修改属性都需要处理 requestLayout（其实有的不需要），暂时这样，后期优化性能问题。
     */
    @JvmOverloads
    fun modifyAttribute(@ColorInt fillColor: Int = 0,
                        @ColorInt startColor: Int = 0,
                        @ColorInt endColor: Int = 0,
                        @ColorInt strokeColor: Int = 0,
                        strokeWidth: Int = 0,
                        cornerRadius: Int = 0) {
        // 背景填充色
        if (fillColor != 0) this.fillColor = fillColor
        // 渐变起始色
        if (startColor != 0) this.startColor = startColor
        if (endColor != 0) this.endColor = endColor
        // 描边颜色
        if (strokeColor != 0) this.strokeColor = strokeColor
        // 描边宽度
        if (strokeWidth != 0) this.strokeWidth = strokeWidth
        // 圆角
        if (cornerRadius != 0) this.cornerRadius = cornerRadius
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val radii = getCornerRadiusByPosition()
        // 初始化shape
        with(gradientDrawable) {
            // 渐变色
            if (startColor != 0xFFFFFFFF.toInt() || endColor != 0xFFFFFFFF.toInt()) {
                colors = intArrayOf(startColor, endColor)
                when (this@ShapeConstraintLayout.orientation) {
                    0 -> orientation = GradientDrawable.Orientation.TOP_BOTTOM
                    1 -> orientation = GradientDrawable.Orientation.TR_BL
                    2 -> orientation = GradientDrawable.Orientation.RIGHT_LEFT
                    3 -> orientation = GradientDrawable.Orientation.BR_TL
                    4 -> orientation = GradientDrawable.Orientation.BOTTOM_TOP
                    5 -> orientation = GradientDrawable.Orientation.BL_TR
                    6 -> orientation = GradientDrawable.Orientation.LEFT_RIGHT
                    7 -> orientation = GradientDrawable.Orientation.TL_BR
                }
            }
            // 填充色
            else {
                setColor(fillColor)
            }
            when (shapeMode) {
                0 -> shape = GradientDrawable.RECTANGLE
                1 -> shape = GradientDrawable.OVAL
                2 -> shape = GradientDrawable.LINE
                3 -> shape = GradientDrawable.RING
            }
            // 统一设置圆角半径
            if (this@ShapeConstraintLayout.cornerPosition == -1) {
                cornerRadius = this@ShapeConstraintLayout.cornerRadius.toFloat()
            }
            // 根据圆角位置设置圆角半径
            else {
                cornerRadii = radii
            }
            // 默认的透明边框不绘制
            if (strokeColor != 0) {
                setStroke(strokeWidth, strokeColor)
            }
        }

        // 设置阴影
        if (shadowBlurRadius > 0) {
            if (shadowRadii == null || shadowRadii?.size != 8) {
                shadowRadii = radii
            }
            val shadowRadii = shadowRadii ?: radii
            applyLocalParamsToShadow(shadowRadii)
        } else {
            // 设置背景
            background = gradientDrawable
        }
    }

    fun getShadowParam(): ShadowRoundRectDrawable.Params {
        return shadowRoundRectDrawable.params
    }

    fun applyShadowParam(newParams: ShadowRoundRectDrawable.Params) {
        // 先把变量保存到本地，再从本地取出变量。
        copyShadowParamsToLocal(newParams)
        applyLocalParamsToShadow(shadowRadii ?: getCornerRadiusByPosition())
    }

    private fun copyShadowParamsToLocal(params: ShadowRoundRectDrawable.Params) {
        shadowDx = params.dx
        shadowDy = params.dy
        shadowRadii = params.outerRadii
        shadowColor = params.shadowColor
        shadowBlurRadius = params.shadowRadius
        excludeDx = params.excludeDx
    }

    private fun copyLocalParamsToShadow(params: ShadowRoundRectDrawable.Params) {
        params.dx = shadowDx
        params.dy = shadowDy
        params.shadowColor = shadowColor
        params.shapeColor = Color.TRANSPARENT
        params.shadowRadius = shadowBlurRadius
        params.excludeDx = excludeDx
    }

    private fun applyLocalParamsToShadow(radii: FloatArray?) {
        val params = shadowRoundRectDrawable.params
        copyLocalParamsToShadow(params)
        params.outerRadii = radii ?: getCornerRadiusByPosition()
        shadowRoundRectDrawable.applyParam(params)
        shadowRoundRectDrawable.applyLayerDrawableInset(0, shadowLayerDrawable)
        shadowRoundRectDrawable.applyLayerDrawableInset(1, shadowLayerDrawable)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && shadowRoundRectDrawable.paint != null) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, shadowPaint)
        }
        background = shadowLayerDrawable
    }

    /**
     * 根据圆角位置获取圆角半径
     */
    private fun getCornerRadiusByPosition(): FloatArray {
        val result = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val cornerRadius = cornerRadius.toFloat()
        if (containsFlag(cornerPosition, TOP_LEFT)) {
            result[0] = cornerRadius
            result[1] = cornerRadius
        }
        if (containsFlag(cornerPosition, TOP_RIGHT)) {
            result[2] = cornerRadius
            result[3] = cornerRadius
        }
        if (containsFlag(cornerPosition, BOTTOM_RIGHT)) {
            result[4] = cornerRadius
            result[5] = cornerRadius
        }
        if (containsFlag(cornerPosition, BOTTOM_LEFT)) {
            result[6] = cornerRadius
            result[7] = cornerRadius
        }
        return result
    }

    /**
     * 是否包含对应flag
     */
    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }
}