package com.hongmei.garbagesort.search

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class RecyclableFragment : BaseFragment<BaseViewModel>() {
    override fun layoutId(): Int {
        return R.layout.recyclable_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}