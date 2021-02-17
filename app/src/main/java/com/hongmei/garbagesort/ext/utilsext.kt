package com.hongmei.garbagesort.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.TypedValue
import com.hongmei.garbagesort.App

/**
 * Author: nanchen
 * Email: liushilin.nanchen@bytedance.com
 * Date: 2021-02-02
 * Desc:
 */
val appContext: Context = App.instance.applicationContext
val res: Resources = appContext.resources

inline val Number.dpF
    get() = toPxF()

inline val Number.dp
    get() = toPx()

fun Number.toPx() = toPxF().toInt()

fun Number.toPxF() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), res.displayMetrics)

fun Number.toDp() = toDpF().toInt()

fun Number.toDpF() = this.toFloat() * DisplayMetrics.DENSITY_DEFAULT / res.displayMetrics.densityDpi

fun Float.lerp(other: Float, amount: Float): Float = this + amount * (other - this)

fun String?.toIntExt(defaultValue: Int = 0): Int = try {
    this?.toInt() ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

fun String?.toColor(defaultValue: Int = Color.parseColor("#1f2229")): Int = try {
    Color.parseColor(this)
} catch (e: Exception) {
    defaultValue
}

fun defense(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

// 泛型实例化
inline fun <reified T : Any> new(): T = T::class.java.getDeclaredConstructor().apply { isAccessible = true }.newInstance()

// DSL 特性
inline fun <reified T : Any> new(block: T.() -> Unit) = new<T>().also(block)