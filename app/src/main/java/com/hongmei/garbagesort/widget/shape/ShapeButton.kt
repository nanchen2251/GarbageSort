package com.hongmei.garbagesort.widget.shape

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.TouchDelegate
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.ext.toPx
import com.hongmei.garbagesort.widget.shape.ShapeConst.BOTTOM_LEFT
import com.hongmei.garbagesort.widget.shape.ShapeConst.BOTTOM_RIGHT
import com.hongmei.garbagesort.widget.shape.ShapeConst.TOP_LEFT
import com.hongmei.garbagesort.widget.shape.ShapeConst.TOP_RIGHT

/**
 * 圆角 button，不设置点击事件同 TextView
 *
 * Author: nanchen
 * Email: liushilin.nanchen@bytedance.com
 * Date: 2019-10-14 11:10
 */
class ShapeButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    /**
     * shape模式
     * 矩形（rectangle）、椭圆形(oval)、线形(line)、环形(ring)
     */
    private var shapeMode = 0

    /**
     * 填充颜色
     */
    private var fillColor = 0

    /**
     * 按压颜色
     */
    private var pressedColor = 0

    /**
     * 描边颜色
     */
    var strokeColor = 0

    /**
     * 描边宽度
     */
    private var strokeWidth = 0

    /**
     * 圆角半径
     */
    private var cornerRadius = 0

    /**
     * 圆角位置
     */
    private var cornerPosition = 0

    /**
     * 点击动效
     */
    private var activeEnable = false

    /**
     * 起始颜色
     */
    private var startColor = 0

    /**
     * 结束颜色
     */
    private var endColor = 0

    /**
     * 阴影相关配置
     * */
    private var shadowDx = 0
    private var shadowDy = 0
    private var shadowBlurRadius = 0
    private var shapeShadowColor = Color.TRANSPARENT
    private var excludeDx = false

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
    private var orientation = 0

    /**
     * drawable位置
     * -1-null、0-left、1-top、2-right、3-bottom
     */
    private var drawablePosition = -1

    /**
     * 普通shape样式
     */
    private val normalGradientDrawable: GradientDrawable by lazy(LazyThreadSafetyMode.NONE) { GradientDrawable() }

    private val shadowRoundRectDrawable: ShadowRoundRectDrawable by lazy(LazyThreadSafetyMode.NONE) {
        ShadowRoundRectDrawable(ShadowRoundRectDrawable.Params(floatArrayOf()))
    }

    private val shadowGradientLayer: LayerDrawable by lazy(LazyThreadSafetyMode.NONE) {
        LayerDrawable(arrayOf(shadowRoundRectDrawable, normalGradientDrawable))
    }

    // button内容总宽度
    private var contentWidth = 0f

    // button内容总高度
    private var contentHeight = 0f

    //避免有些包含wrap_content的界面刷新导致反复调用onLayout-changeTintContextWrapperToActivity
    private var hasChangeContext = false

    /**
     * 触控增加区域
     */
    private var touchAdd = 0
    private var touchAddBottom = 0
    private var touchAddLeft = 0
    private var touchAddRight = 0
    private var touchAddTop = 0

    //避免 DrawAllocation warning
    private var touchRect: Rect? = null
    private val touchDelegateCustom: TouchDelegate by lazy(LazyThreadSafetyMode.NONE) { TouchDelegate(touchRect, this) }
    private var centerText = true

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ShapeButton).apply {
            shapeMode = getInt(R.styleable.ShapeButton_sb_shapeMode, 0)
            fillColor = getColor(R.styleable.ShapeButton_sb_fillColor, 0xFFFFFFFF.toInt())
            pressedColor = getColor(R.styleable.ShapeButton_sb_pressedColor, 0xFF666666.toInt())
            strokeColor = getColor(R.styleable.ShapeButton_sb_strokeColor, 0)
            strokeWidth = getDimensionPixelOffset(R.styleable.ShapeButton_sb_strokeWidth, 0)
            cornerRadius = getDimensionPixelSize(R.styleable.ShapeButton_sb_cornerRadius, 0)
            cornerPosition = getInt(R.styleable.ShapeButton_sb_cornerPosition, -1)
            activeEnable = getBoolean(R.styleable.ShapeButton_sb_activeEnable, false)
            drawablePosition = getInt(R.styleable.ShapeButton_sb_drawablePosition, -1)
            startColor = getColor(R.styleable.ShapeButton_sb_startColor, 0xFFFFFFFF.toInt())
            endColor = getColor(R.styleable.ShapeButton_sb_endColor, 0xFFFFFFFF.toInt())
            orientation = getColor(R.styleable.ShapeButton_sb_orientation, 0)
            touchAdd = getDimensionPixelSize(R.styleable.ShapeButton_sb_touchAdd, 0)
            touchAddBottom = getDimensionPixelSize(R.styleable.ShapeButton_sb_touchAddBottom, touchAdd)
            touchAddLeft = getDimensionPixelSize(R.styleable.ShapeButton_sb_touchAddLeft, touchAdd)
            touchAddRight = getDimensionPixelSize(R.styleable.ShapeButton_sb_touchAddRight, touchAdd)
            touchAddTop = getDimensionPixelSize(R.styleable.ShapeButton_sb_touchAddTop, touchAdd)
            centerText = getBoolean(R.styleable.ShapeButton_sb_centerText, true)
            shapeShadowColor = getColor(R.styleable.ShapeButton_sb_shadow_color, Color.TRANSPARENT)
            excludeDx = getBoolean(R.styleable.ShapeButton_sb_exclude_dx, false)
            shadowDx = getDimensionPixelSize(R.styleable.ShapeButton_sb_shadow_dx, 0)
            shadowDy = getDimensionPixelSize(R.styleable.ShapeButton_sb_shadow_dy, 0)
            shadowBlurRadius = getDimensionPixelSize(R.styleable.ShapeButton_sb_shadow_blur_radius, 0)
            if (touchAddBottom > 0 && touchAddLeft > 0 && touchAddRight > 0 && touchAddTop > 0) {
                touchRect = Rect()
            }
            initShadowParam()
            recycle()
        }
    }

    private fun initShadowParam() {
        val params = shadowRoundRectDrawable.params
        params.dx = shadowDx
        params.dy = shadowDy
        params.shadowColor = shapeShadowColor
        params.shapeColor = Color.TRANSPARENT
        params.shadowRadius = shadowBlurRadius
        params.excludeDx = excludeDx
    }

    /**
     * 在代码中修改属性
     * 由于在代码中修改属性都需要处理 requestLayout（其实有的不需要），暂时这样，后期优化性能问题。
     */
    @JvmOverloads
    fun modifyAttribute(
            @ColorInt fillColor: Int = 0,
            @ColorInt startColor: Int = 0,
            @ColorInt endColor: Int = 0,
            @ColorInt strokeColor: Int = 0,
            strokeWidth: Int = 0,
            cornerRadius: Int = 0,
            @ColorInt textColor: Int = 0
    ) {
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
        // 字体颜色
        if (textColor != 0) this.setTextColor(textColor)
        requestLayout()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateBackground()
    }

    private fun updateBackground() {
        // 初始化normal状态
        val radii = getCornerRadiusByPosition()
        with(normalGradientDrawable) {
            // 渐变色
            if (startColor != 0xFFFFFFFF.toInt() || endColor != 0xFFFFFFFF.toInt()) {
                colors = intArrayOf(startColor, endColor)
                when (this@ShapeButton.orientation) {
                    0 -> orientation = GradientDrawable.Orientation.TOP_BOTTOM
                    1 -> orientation = GradientDrawable.Orientation.TR_BL
                    2 -> orientation = GradientDrawable.Orientation.RIGHT_LEFT
                    3 -> orientation = GradientDrawable.Orientation.BR_TL
                    4 -> orientation = GradientDrawable.Orientation.BOTTOM_TOP
                    5 -> orientation = GradientDrawable.Orientation.BL_TR
                    6 -> orientation = GradientDrawable.Orientation.LEFT_RIGHT
                    7 -> orientation = GradientDrawable.Orientation.TL_BR
                }
            } else {// 填充色
                setColor(fillColor)
            }
            when (shapeMode) {
                0 -> shape = GradientDrawable.RECTANGLE
                1 -> shape = GradientDrawable.OVAL
                2 -> shape = GradientDrawable.LINE
                3 -> shape = GradientDrawable.RING
            }
            // 统一设置圆角半径
            if (this@ShapeButton.cornerPosition == -1) {
                cornerRadius = this@ShapeButton.cornerRadius.toFloat()
            } else { // 根据圆角位置设置圆角半径
                cornerRadii = radii
            }
            // 默认的透明边框不绘制,否则会导致没有阴影
            if (strokeColor != 0) {
                setStroke(strokeWidth, strokeColor)
            }
        }

        // 是否开启点击动效
        background = if (activeEnable) {
            // 5.0以上水波纹效果
            RippleDrawable(ColorStateList.valueOf(pressedColor), normalGradientDrawable, null)

        } else {
            val drawable: Drawable
            // 设置阴影
            if (shadowBlurRadius > 0) {
                val params = shadowRoundRectDrawable.params
                params.outerRadii = radii
                shadowRoundRectDrawable.applyParam(params)
                shadowRoundRectDrawable.applyLayerDrawableInset(0, shadowGradientLayer)
                shadowRoundRectDrawable.applyLayerDrawableInset(1, shadowGradientLayer)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && shadowRoundRectDrawable.paint != null) {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
                }
                drawable = shadowGradientLayer
            } else {
                drawable = normalGradientDrawable
            }
            drawable
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 如果xml中配置了drawable则设置padding让文字移动到边缘与drawable靠在一起
        // button中配置的drawable默认贴着边缘
        if (drawablePosition > -1) {
            compoundDrawables.let {
                compoundDrawables[drawablePosition]?.let {
                    // 图片间距
                    val drawablePadding = compoundDrawablePadding
                    when (drawablePosition) {
                        // 左右drawable
                        0, 2 -> {
                            // 图片宽度
                            val drawableWidth = it.intrinsicWidth
                            // 获取文字宽度
                            val textWidth = paint.measureText(text.toString())
                            // 内容总宽度
                            contentWidth = textWidth + drawableWidth + drawablePadding
                            val rightPadding = (width - contentWidth).toInt()
                            // 图片和文字全部靠在左侧
                            setPadding(0, 0, rightPadding, 0)
                        }
                        // 上下drawable
                        1, 3 -> {
                            // 图片高度
                            val drawableHeight = it.intrinsicHeight
                            // 获取文字高度
                            val fm = paint.fontMetrics
                            // 单行高度
                            val singeLineHeight =
                                    Math.ceil(fm.descent.toDouble() - fm.ascent.toDouble()).toFloat()
                            // 总的行间距
                            val totalLineSpaceHeight = (lineCount - 1) * lineSpacingExtra
                            val textHeight = singeLineHeight * lineCount + totalLineSpaceHeight
                            // 内容总高度
                            contentHeight = textHeight + drawableHeight + drawablePadding
                            // 图片和文字全部靠在上侧
                            val bottomPadding = (height - contentHeight).toInt()
                            setPadding(0, 0, 0, bottomPadding)
                        }
                    }
                }

            }
        }
        if (centerText) {
            // 内容居中
            gravity = Gravity.CENTER
        }
        // 可点击
        if (activeEnable) {
            isClickable = true
        }

        //增加触控区域
        if (changed) {
            touchRect?.apply {
                this.left = left - touchAddLeft.toPx()
                this.top = top - touchAddTop.toPx()
                this.right = right + touchAddRight.toPx()
                this.bottom = bottom + touchAddBottom.toPx()
                val parent = this@ShapeButton.parent as View
                parent.touchDelegate = touchDelegateCustom
            }
        }
        changeTintContextWrapperToActivity()
    }

    fun updateShadowParam(shadowParam: ShadowRoundRectDrawable.Params) {
        shadowRoundRectDrawable.applyParam(shadowParam)
    }

    fun getShadowParam(): ShadowRoundRectDrawable.Params {
        return shadowRoundRectDrawable.params
    }

    /**
     * 更新渐变色 并重绘
     */
    public fun updateBgColor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // 让图片和文字居中
        var canvasSave = false
        when {
            contentWidth > 0 && (drawablePosition == 0 || drawablePosition == 2) -> {
                canvasSave = true
                canvas.save()
                canvas.translate((width - contentWidth) / 2, 0f)
            }
            contentHeight > 0 && (drawablePosition == 1 || drawablePosition == 3) -> {
                canvasSave = true
                canvas.save()
                canvas.translate(0f, (height - contentHeight) / 2)
            }
            shadowBlurRadius > 0 && !activeEnable -> {
                canvasSave = true
                canvas.save()
                val verticalOffset = (-(paddingTop - shadowRoundRectDrawable.desiredTopPadding)
                        + paddingBottom - (shadowRoundRectDrawable.desiredBottomPadding)) / 2
                val horizontalOffset = (-(paddingLeft - shadowRoundRectDrawable.desiredLeftPadding)
                        + (paddingRight - shadowRoundRectDrawable.desiredRightPadding)) / 2
                canvas.translate(horizontalOffset, verticalOffset)
            }
        }
        super.onDraw(canvas)

        if (canvasSave) {
            canvas.restore()
        }
    }

    /**
     * 从support23.3.0开始View中的getContext方法返回的是TintContextWrapper而不再是Activity
     * 如果使用xml注册onClick属性，就会通过反射到Activity中去找对应的方法
     * 5.0以下系统会反射到TintContextWrapper中去找对应的方法，程序直接crash
     * 所以这里需要针对5.0以下系统单独处理View中的getContext返回值
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun changeTintContextWrapperToActivity() {
        if (!hasChangeContext && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getActivity()?.let {
                var clazz: Class<*>? = this::class.java
                while (clazz != null) {
                    try {
                        val field = clazz.getDeclaredField("mContext")
                        field.isAccessible = true
                        field.set(this, it)
                        hasChangeContext = true;
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    clazz = clazz.superclass
                }
            }
        }
    }

    /**
     * 从context中得到真正的activity
     */
    private fun getActivity(): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
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
