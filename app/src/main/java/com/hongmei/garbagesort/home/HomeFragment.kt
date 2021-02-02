package com.hongmei.garbagesort.home

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-02
 * Desc: 主页
 */
class HomeFragment : BaseFragment<HomeViewModel>() {
    override fun layoutId(): Int {
        return R.layout.home_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}