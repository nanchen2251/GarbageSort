package com.hongmei.garbagesort.splash

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.hongmei.garbagesort.MainActivity
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.util.CacheUtil
import com.hongmei.garbagesort.util.SettingUtil
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.welcome_activity.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.view.gone
import me.hgj.jetpackmvvm.ext.view.visible

class WelcomeActivity : BaseActivity<BaseViewModel>() {
    private var resList = arrayOf("垃圾", "分类", "还看\n垃圾分类宝")

    private lateinit var bannerViewPager: BannerViewPager<String, WelcomeBannerViewHolder>

    override fun layoutId(): Int {
        return R.layout.welcome_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        bannerViewPager = findViewById(R.id.bannerVp)
        welcomeBaseView.setBackgroundColor(SettingUtil.getColor(this))
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                // 带点渐变动画
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 3000)
            welcomeJumpText.setOnClickListener {
                ProxyClick().toMain()
            }
        }
        welcomeJoin.setOnClickListener {
            ProxyClick().toMain()
        }
    }

    inner class ProxyClick {
        fun toMain() {
            CacheUtil.setFirst(false)
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
            //带点渐变动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}