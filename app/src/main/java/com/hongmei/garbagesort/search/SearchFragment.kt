package com.hongmei.garbagesort.search

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment

/**
 * Date: 2021-02-02
 * Desc: 搜索页面
 */
class SearchFragment : BaseFragment<SearchViewModel>() {
    override fun layoutId(): Int {
        return R.layout.search_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

}