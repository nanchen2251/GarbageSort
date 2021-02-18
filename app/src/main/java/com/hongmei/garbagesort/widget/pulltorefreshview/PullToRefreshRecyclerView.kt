package com.hongmei.garbagesort.widget.pulltorefreshview

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hongmei.garbagesort.widget.pulltozoomview.SlideRecyclerView
import kotlin.math.abs

class PullToRefreshRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PullToRefreshBase<RecyclerView>(context, attrs) {
    private var headerHeight = 0
    private var footerHeight = 0
    private var maxPullFooterHeight = Int.MAX_VALUE
    private var maxPullHeaderHeight = Int.MAX_VALUE
    private var headerContainer: FrameLayout? = null
    private var footerContainer: FrameLayout? = null
    private val scrollHeaderRunnable: ScrollUpRunnable
    private val scrollFooterRunnable: ScrollDownRunnable
    var releaseToRefreshThreshold = 0.6F
    var onPullListener: OnPullListener? = null

    private val sSmoothToTopInterpolator = Interpolator { paramAnonymousFloat ->
        val f = paramAnonymousFloat - 1.0f
        1.0f + f * (f * (f * (f * f)))
    }

    private val springInterceptor = SpringInterpolator()

    init {
        scrollHeaderRunnable = ScrollUpRunnable()
        scrollFooterRunnable = ScrollDownRunnable()
    }

    override fun createRootView(context: Context?, attrs: AttributeSet?): RecyclerView {
        val rv = SlideRecyclerView(context!!, attrs)
        // Set it to this so it can be used in ListActivity/ListFragment
        // 不设置，fragment被回收后，会crash!!
        rv.id = android.R.id.list
        rv.overScrollMode = View.OVER_SCROLL_NEVER
        return rv
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (headerContainer != null) {
            headerHeight = headerContainer?.measuredHeight ?: 0
        }

        if (footerContainer != null) {
            footerHeight = footerContainer?.measuredHeight ?: 0
        }
    }

    override fun onPullDownRelease() {
        if (maxPullHeaderHeight != Int.MAX_VALUE) {
            if (headerHeight >= maxPullHeaderHeight * releaseToRefreshThreshold) {
                // 开始刷新数据
                onPullListener?.onPullHeaderDownRelease(true)
            } else {
                onPullListener?.onPullHeaderDownRelease(false)
                // 没达到阈值，这个时候开始回弹
                smoothScrollHeaderBack(100)
            }
        } else {
            onPullListener?.onPullHeaderDownRelease(false)
            smoothScrollHeaderBack(100)
        }
    }

    fun smoothScrollHeaderBack(duration: Long = 100) {
        scrollHeaderRunnable.startAnimation(duration)
    }

    override fun onPullUpRelease() {
        if (maxPullFooterHeight != Int.MAX_VALUE) {
            if (footerHeight >= maxPullFooterHeight * releaseToRefreshThreshold) {
                // 开始刷新数据
                onPullListener?.onPullFooterUpRelease(true)
            } else {
                // 没达到阈值，这个时候开始回弹
                smoothScrollFooterBack(100)
                onPullListener?.onPullFooterUpRelease(false)
            }
        } else {
            smoothScrollFooterBack(100)
            onPullListener?.onPullFooterUpRelease(false)
        }
    }

    fun smoothScrollFooterBack(duration: Long = 100) {
        scrollFooterRunnable.startAnimation(duration)
    }

    override fun pullDownHeader(newScrollValue: Int) {
        if (!scrollHeaderRunnable.isFinished) {
            scrollHeaderRunnable.abortAnimation()
        }
        val headerLp = getHeaderView()?.layoutParams
        var newHeight = abs(newScrollValue)
        val calculateValue = springInterceptor.getInterpolation(newHeight.toFloat() / maxPullHeaderHeight)
        newHeight = (calculateValue * newHeight).toInt()
        if (newHeight > maxPullHeaderHeight) {
            newHeight = maxPullHeaderHeight
        }
        headerHeight = newHeight
        val enableRefresh = (maxPullHeaderHeight != Int.MAX_VALUE && headerHeight >= maxPullHeaderHeight * releaseToRefreshThreshold)
        onPullListener?.onPullingHeaderDown(newHeight, enableRefresh)
        headerLp?.height = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        getHeaderView()?.layoutParams = headerLp
    }

    override fun isReadyForPullDown(): Boolean {
        return isFirstItemVisible()
    }

    private fun isFirstItemVisible(): Boolean {
        if (refreshRootView is RecyclerView) {
            val adapter = refreshRootView?.adapter ?: return false
            val layoutManager = refreshRootView?.layoutManager as? LinearLayoutManager
            if (adapter.itemCount == 0) {
                return false
            } else if (layoutManager != null && layoutManager.findFirstVisibleItemPosition() <= 1) {
                /*
                  This check should really just be:
                  mRootView.getFirstVisiblePosition() == 0, but PtRListView
                  internally use a HeaderView which messes the positions up. For
                  now we'll just add one to account for it and rely on the inner
                  condition which checks getTop().
                 */
                val firstVisibleChild = refreshRootView?.getChildAt(0)
                if (firstVisibleChild != null) {
                    return firstVisibleChild.top >= refreshRootView!!.top
                }
            }
        }
        return false
    }

    fun getRecyclerView(): RecyclerView? {
        return refreshRootView
    }


    override fun isReadyForPullUp(): Boolean {
        return isScrollToBottom()
    }

    private fun isScrollToBottom(): Boolean {
        if (refreshRootView is RecyclerView) {
            val adapter = (refreshRootView?.adapter as? BaseQuickAdapter<*, *>) ?: return false
            val layoutManager = refreshRootView?.layoutManager as? LinearLayoutManager
            if (adapter.itemCount == 0) {
                return false
            } else if (layoutManager != null) {
                val childrenCount = adapter.itemCount
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisible >= childrenCount - 1 - adapter.headerLayoutCount) return true
            }
        }
        return false
    }

    override fun pullUpFooter(newScrollValue: Int) {
        if (!scrollFooterRunnable.isFinished) {
            scrollFooterRunnable.abortAnimation()
        }
        val footerLp = getFooterView()?.layoutParams
        var newHeight = abs(newScrollValue)
        val calculateValue = springInterceptor.getInterpolation(newHeight.toFloat() / maxPullHeaderHeight)
        newHeight = (calculateValue * newHeight).toInt()
        if (newHeight > maxPullFooterHeight) {
            newHeight = maxPullFooterHeight
        }
        footerHeight = newHeight
        val enableLoadMore = (maxPullFooterHeight != Int.MAX_VALUE && footerHeight >= maxPullFooterHeight * releaseToRefreshThreshold)
        onPullListener?.onPullingFooterUp(newHeight, enableLoadMore)
        footerLp?.height = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        getFooterView()?.layoutParams = footerLp
        getRecyclerView()?.scrollBy(0, newHeight)
    }

    override fun setHeaderView(headerView: View) {
        super.setHeaderView(headerView)
        updateHeaderView(headerView)
    }

    private fun updateHeaderView(headerView: View?) {
        if (refreshRootView != null && refreshRootView?.adapter is BaseQuickAdapter<*, *>) {
            val adapter = refreshRootView?.adapter as? BaseQuickAdapter<*, *>?
            headerContainer?.let { adapter?.removeHeaderView(it) }

            headerContainer?.removeAllViews()
            if (headerView != null) {
                val height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
                val containerLp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                containerLp.gravity = Gravity.BOTTOM

                headerContainer?.addView(headerView, containerLp)
            }
            headerContainer?.let { adapter?.addHeaderView(it) }
        }
    }

    override fun setFooterView(footerView: View) {
        super.setFooterView(footerView)
        updateFooterView(footerView)
    }

    // 这里处理很粗暴，要求传入的 footer/header 必须是确定的宽高。之后有时间要把这里优化好
    fun setMaxPullFooterHeight(height: Int) {
        maxPullFooterHeight = height
    }

    fun setMaxPullHeaderHeight(height: Int) {
        maxPullHeaderHeight = height
    }

    private fun updateFooterView(footerView: View?) {
        if (refreshRootView != null && refreshRootView?.adapter is BaseQuickAdapter<*, *>) {
            val adapter = refreshRootView?.adapter as? BaseQuickAdapter<*, *>?
            footerContainer?.let { adapter?.removeFooterView(it) }

            footerContainer?.removeAllViews()
            if (footerView != null) {
                val height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
                val containerLp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                containerLp.gravity = Gravity.TOP

                footerContainer?.addView(footerView, containerLp)
            }
            footerContainer?.let { adapter?.addFooterView(it) }
        }
    }

    override fun handleStyledAttributes(a: TypedArray?) {
        headerContainer = FrameLayout(context)

        if (getHeaderView() != null) {
            headerContainer?.addView(getHeaderView())
        }

        val adapter = refreshRootView?.adapter
        (adapter as? BaseQuickAdapter<*, *>)?.addHeaderView(headerContainer!!)

        footerContainer = FrameLayout(context)
        if (getFooterView() != null) {
            footerContainer?.addView(getFooterView())
        }

        (adapter as? BaseQuickAdapter<*, *>)?.addFooterView(footerContainer!!)
    }

    /**
     * 向上滑动的 runnable
     * */
    inner class ScrollUpRunnable : ScrollBackRunnable() {
        override fun getScrollBackView(): View? {
            return getHeaderView()
        }
    }

    inner class ScrollDownRunnable : ScrollBackRunnable() {
        override fun getScrollBackView(): View? {
            return getFooterView()
        }
    }


    /**
     * 回滚的 runnable，上拉、下拉回弹时使用
     * */
    abstract inner class ScrollBackRunnable : Runnable {
        var duration: Long = 0
        var isFinished = true
        var startTime: Long = 0
        var targetValue = 0
        fun abortAnimation() {
            isFinished = true
            postOnAnimation(this)
        }

        abstract fun getScrollBackView(): View?

        override fun run() {
            if (getScrollBackView() != null && !isFinished) {
                val fraction = (SystemClock.currentThreadTimeMillis() - startTime) / duration.toFloat()
                if (fraction >= 1) {
                    setHeaderHeight(0)
                    return
                }
                val smoothFraction = sSmoothToTopInterpolator.getInterpolation(fraction)
                val currentValue = targetValue - targetValue * smoothFraction
                setHeaderHeight(currentValue.toInt())
                postOnAnimation(this)
            }
        }

        private fun setHeaderHeight(height: Int) {
            val viewLp = getScrollBackView()?.layoutParams
            viewLp?.height = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            getScrollBackView()?.layoutParams = viewLp
        }

        fun startAnimation(animationDuration: Long) {
            val view = getScrollBackView() ?: return
            startTime = SystemClock.currentThreadTimeMillis()
            duration = animationDuration
            isFinished = false
            targetValue = view.measuredHeight
            postOnAnimation(this)
        }
    }

    interface OnPullListener {
        fun onPullingHeaderDown(newScrollValue: Int, enableRefresh: Boolean)
        fun onPullHeaderDownRelease(enableRefresh: Boolean)

        fun onPullingFooterUp(newScrollValue: Int, enableLoadMore: Boolean)
        fun onPullFooterUpRelease(enableLoadMore: Boolean)
    }
}