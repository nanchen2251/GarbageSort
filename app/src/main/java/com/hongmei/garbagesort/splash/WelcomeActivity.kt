package com.hongmei.garbagesort.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.hongmei.garbagesort.MainActivity
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.showMessage
import com.hongmei.garbagesort.login.LoginActivity
import com.hongmei.garbagesort.util.CacheUtil
import com.hongmei.garbagesort.util.SettingUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.welcome_activity.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.view.gone
import me.hgj.jetpackmvvm.ext.view.visible

class WelcomeActivity : BaseActivity<BaseViewModel>() {
    private var resList = arrayOf("垃圾", "分类", "还看\n垃圾分类宝")
    private var hasGoSecondPage = false
    private var hasPermissions = false

    private lateinit var bannerViewPager: BannerViewPager<String, WelcomeBannerViewHolder>

    override fun layoutId(): Int {
        return R.layout.welcome_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        bannerViewPager = findViewById(R.id.bannerVp)
        welcomeBaseView.setBackgroundColor(SettingUtil.getColor(this))
        initPermissions()
        if (CacheUtil.isFirst()) {
            // 是第一次打开App 显示引导页
            welcomeImage.gone()
            welcomeJumpText.gone()
            bannerViewPager.apply {
                adapter = WelcomeBannerAdapter()
                setLifecycleRegistry(lifecycle)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (position == resList.size - 1) {
                            welcomeJoin.visible()
                        } else {
                            welcomeJoin.gone()
                        }
                    }
                })
                create(resList.toList())
            }
        } else {
            // 不是第一次打开App 1.5秒后自动跳转到主页
            welcomeImage.visible()
            welcomeJumpText.visible()
            bannerViewPager.postDelayed({
                toSecondPage()
            }, 3000)
            welcomeJumpText.setOnClickListener {
                toSecondPage()
            }
        }
        welcomeJoin.setOnClickListener {
            toSecondPage()
        }
    }

    private fun initPermissions() {
        RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            ).subscribe {
                if (!it) {
                    showMessage("需要开启所有申请的权限，否则部分功能无法使用")
                }
            }
    }

    private fun toSecondPage() {
        if (hasGoSecondPage) {
            return
        }
        hasGoSecondPage = true
        if (CacheUtil.isLogin()) {
            toMain()
        } else {
            toLogin()
        }
    }

    private fun toMain() {
        CacheUtil.setFirst(false)
        startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        finish()
        //带点渐变动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun toLogin() {
        CacheUtil.setFirst(false)
        startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        finish()
        //带点渐变动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
        private const val BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION"
    }

}