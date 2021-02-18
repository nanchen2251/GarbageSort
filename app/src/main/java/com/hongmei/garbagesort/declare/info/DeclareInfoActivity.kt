package com.hongmei.garbagesort.declare.info

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.initClose
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * 信息申报发布页面
 */
class DeclareInfoActivity : BaseActivity<DeclareInfoViewModel>() {

    override fun layoutId(): Int {
        return R.layout.declare_info_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            toolbar.setBackgroundColor(it)
        }
        toolbar.initClose("信息申报") {
            finish()
        }
    }
}