package com.hongmei.garbagesort.widget.pulltozoomview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class SlideRecyclerView : androidx.recyclerview.widget.RecyclerView {

    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private var xDistance: Float = 0.toFloat()
    private var yDistance: Float = 0.toFloat()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                yDistance = 0f
                xDistance = yDistance
                lastX = ev.getX()
                lastY = ev.getY()
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.getX()
                val curY = ev.getY()
                xDistance += Math.abs(curX - lastX)
                yDistance += Math.abs(curY - lastY)
                lastX = curX
                lastY = curY
                if (xDistance * 2.5 > yDistance) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}