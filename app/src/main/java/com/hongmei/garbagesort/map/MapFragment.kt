package com.hongmei.garbagesort.map

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-02
 * Desc: 地图页面
 */
class MapFragment : BaseFragment<MapViewModel>() {
    override fun layoutId(): Int {
        return R.layout.map_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}