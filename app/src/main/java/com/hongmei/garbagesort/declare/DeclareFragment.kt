package com.hongmei.garbagesort.declare

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-02
 * Desc: 信息发布页面
 */
class DeclareFragment : BaseFragment<DeclareViewModel>() {
    override fun layoutId(): Int {
        return R.layout.declare_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}