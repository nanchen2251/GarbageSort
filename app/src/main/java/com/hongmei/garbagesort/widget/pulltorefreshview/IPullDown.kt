package com.hongmei.garbagesort.widget.pulltorefreshview

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

interface IPullDown {
    fun setHeaderView(headerView: View)

    fun getHeaderView(): View?

    fun isPullDownEnabled(): Boolean

    fun isHideHeader(): Boolean

    fun onPullDownRelease()

}