package com.hongmei.garbagesort.mine.about

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.initClose
import com.hongmei.garbagesort.ext.toastNormal
import kotlinx.android.synthetic.main.about_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * 关于我们页面
 */
class AboutActivity : BaseActivity<BaseViewModel>() {

    override fun layoutId(): Int {
        return R.layout.about_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        toolbar.initClose("关于我们") {
            finish()
        }
        aboutCheckVersionLayout.setOnClickListener {
            toastNormal("当前已经是最新版本")
        }
        aboutScoreLayout.setOnClickListener {
            toastNormal("当前暂未开通此功能")
        }
    }
}