package com.hongmei.garbagesort.widget.pulltorefreshview

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

interface IPullUp {
    fun setFooterView(footerView: View)

    fun getFooterView(): View?

    fun isPullUpEnabled(): Boolean

    fun isHideFooter(): Boolean

    fun onPullUpRelease()

}