package com.hongmei.garbagesort.widget.pulltorefreshview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hongmei.garbagesort.R
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

abstract class PullToRefreshBase<T : View?> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs), IPullDown, IPullUp {
    protected var refreshRootView: T? = null

    //头部View
    private var header: View? = null
    private var footer: View? = null
    private var isPullDownEnabled = true
    private var isPullUpEnable = true
    private var isParallax = true
    private var isZooming = false
    private var isPullingDown = false
    private var isPullingUp = false
    private var isHideHeader = false
    private var isHideFooter = false
    private var mTouchSlop = 0
    private var mIsBeingDragged = false
    private var mLastMotionY = 0f
    private var mLastMotionX = 0f
    private var mInitialMotionY = 0f
    private var mInitialMotionX = 0f
    private fun init(context: Context, attrs: AttributeSet?) {
        gravity = Gravity.CENTER
        val config = ViewConfiguration.get(context)
        mTouchSlop = config.scaledTouchSlop

        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        refreshRootView = createRootView(context, attrs)
        if (attrs != null) {
            val mLayoutInflater = LayoutInflater.from(getContext())
            //初始化状态View
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.PullToRefreshBase)
            val headerViewResId = a.getResourceId(R.styleable.PullToRefreshBase_header_view, 0)
            if (headerViewResId > 0) {
                header = mLayoutInflater.inflate(headerViewResId, null, false)
            }
            val footerViewResId = a.getResourceId(R.styleable.PullToRefreshBase_footer_view, 0)
            if (footerViewResId > 0) {
                footer = mLayoutInflater.inflate(footerViewResId, null, false)
            }

            // Let the derivative classes have a go at handling attributes, then
            // recycle them...
            handleStyledAttributes(a)

            a.recycle()
        }
        addView(refreshRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun isPullDownEnabled(): Boolean {
        return isPullDownEnabled
    }

    override fun isHideHeader(): Boolean {
        return isHideHeader
    }

    override fun isPullUpEnabled(): Boolean {
        return isPullUpEnable
    }

    override fun isHideFooter(): Boolean {
        return isHideFooter
    }

    fun setZoomEnabled(isZoomEnabled: Boolean) {
        this.isPullDownEnabled = isZoomEnabled
    }

    fun setParallax(isParallax: Boolean) {
        this.isParallax = isParallax
    }

    fun setHideHeader(isHideHeader: Boolean) { //header显示才能Zoom
        this.isHideHeader = isHideHeader
    }

    fun setHideFooter(isHideFooter: Boolean) {
        this.isHideFooter = isHideFooter
    }

    override fun setFooterView(footerView: View) {
        this.footer = footerView
    }

    override fun setHeaderView(headerView: View) {
        this.header = headerView
    }

    override fun getHeaderView(): View? {
        return header
    }

    override fun getFooterView(): View? {
        return footer
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val enablePullDown = isPullDownEnabled() && !isHideHeader()
        val enablePullUp = isPullUpEnabled() && !isHideFooter()
        if (!enablePullDown && !enablePullUp) {
            return false
        }
        val action = event.action
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false
            return false
        }
        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (isReadyForPullDown() || isReadyForPullUp()) {
                    val y = event.y
                    val x = event.x
                    val diff: Float
                    val oppositeDiff: Float
                    val absDiff: Float

                    // We need to use the correct values, based on scroll
                    // direction
                    diff = y - mLastMotionY
                    oppositeDiff = x - mLastMotionX
                    absDiff = abs(diff)
                    if (absDiff > mTouchSlop && absDiff > abs(oppositeDiff)) {
                        if (diff >= 1f && enablePullDown && isReadyForPullDown()) {
                            mLastMotionY = y
                            mLastMotionX = x
                            mIsBeingDragged = true
                            isPullingDown = true
                        } else if (diff <= -1 && enablePullUp && isReadyForPullUp()) {
                            mLastMotionY = y
                            mLastMotionX = x
                            mIsBeingDragged = true
                            isPullingUp = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullDown() || isReadyForPullUp()) {
                    mInitialMotionY = event.y
                    mLastMotionY = event.y
                    mInitialMotionX = event.x
                    mLastMotionX = event.x
                    mIsBeingDragged = false
                }
            }
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val interceptPullDown = isPullDownEnabled() && !isHideHeader()
        val interceptPullUp = isPullUpEnabled() && !isHideFooter()
        if (!interceptPullDown && !interceptPullUp) {
            return false
        }
        if (event.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (mIsBeingDragged) {
                    mLastMotionY = event.y
                    mLastMotionX = event.x
                    if (isPullingDown) {
                        pullDownEvent()
                        return true
                    } else if (isPullingUp) {
                        pullUpEvent()
                        return true
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullDown() || isReadyForPullUp()) {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    // If we're already refreshing, just scroll back to the top
                    if (isPullingDown) {
                        onPullDownRelease()
                    } else if (isPullingUp) {
                        onPullUpRelease()
                    }

                    isZooming = false
                    isPullingDown = false
                    isPullingUp = false

                    return true
                }
            }
        }
        return false
    }

    private fun pullDownEvent() {
        val newScrollValue: Int
        val initialMotionValue = mInitialMotionY
        val lastMotionValue = mLastMotionY
        newScrollValue = (min(initialMotionValue - lastMotionValue, 0f) / FRICTION).roundToInt()
        pullDownHeader(newScrollValue)
    }

    private fun pullUpEvent() {
        val newScrollValue: Int
        val initialMotionValue = mInitialMotionY
        val lastMotionValue = mLastMotionY
        newScrollValue = (min(lastMotionValue - initialMotionValue, 0f) / FRICTION).roundToInt()
        pullUpFooter(newScrollValue)
    }

    protected abstract fun createRootView(context: Context?, attrs: AttributeSet?): T
    protected abstract fun pullDownHeader(newScrollValue: Int)
    protected abstract fun isReadyForPullDown(): Boolean
    protected abstract fun isReadyForPullUp(): Boolean
    protected abstract fun pullUpFooter(newScrollValue: Int)
    protected abstract fun handleStyledAttributes(a: TypedArray?)

    companion object {
        private const val FRICTION = 3.0f
    }

    init {
        init(context, attrs)
    }
}
