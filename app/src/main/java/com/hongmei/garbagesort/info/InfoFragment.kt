package com.hongmei.garbagesort.info

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-18
 * Desc: 信息公开页面
 */
class InfoFragment : BaseFragment<InfoViewModel>() {
    override fun layoutId(): Int {
        return R.layout.info_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}