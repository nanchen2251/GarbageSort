package com.hongmei.garbagesort.splash

import android.os.Bundle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class SplashActivity : BaseActivity<BaseViewModel>() {
    override fun layoutId(): Int {
        return R.layout.splash_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}