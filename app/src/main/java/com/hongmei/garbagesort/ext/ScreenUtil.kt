package com.hongmei.garbagesort.ext

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import com.hongmei.garbagesort.util.DeviceUtils

/**
 * Date: 2021-02-18
 * Desc:
 */
/**
 * 屏幕宽度
 */
val screenWidth
    get() = res.displayMetrics.widthPixels

/**
 * 屏幕高度
 */
val screenHeight
    get() = getScreenRealHeight()

fun Activity.getNavigationBarHeight(): Int {
    return DeviceUtils.getNavigationBarHeight(this)
}

fun Activity.getStatusBarHeight(): Int {
    return DeviceUtils.getStatusBarHeight(this)
}


private fun getScreenRealHeight(): Int {
    val wm = appContext.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        ?: return res.displayMetrics.heightPixels
    val point = Point()
    wm.defaultDisplay?.getRealSize(point)
    return point.y
}

/**
 * 屏幕密度
 */
val screenDensity
    get() = res.displayMetrics.density

val screenDensityDpi
    get() = res.displayMetrics.densityDpi