package com.hongmei.garbagesort

import android.os.Bundle
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.toastNormal
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * APP 主页面
 */
class MainActivity : BaseActivity<BaseViewModel>() {
    /**
     * 上次点击返回键的时间
     */
    private var lastBackPressTime = -1L

    override fun layoutId(): Int {
        return R.layout.main_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
    }


    override fun onBackPressed() {
        val currentTIme = System.currentTimeMillis()
        if (lastBackPressTime == -1L || currentTIme - lastBackPressTime >= 2000) {
            // 显示提示信息
            showBackPressTip()
            // 记录时间
            lastBackPressTime = currentTIme
        } else {
            //退出应用
            finish()
        }
    }

    private fun showBackPressTip() {
        toastNormal("再按一次退出")
    }
}