package com.hongmei.garbagesort.mine

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-02
 * Desc: 我的页面
 */
class MineFragment : BaseFragment<MineViewModel>() {
    override fun layoutId(): Int {
        return R.layout.mine_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}