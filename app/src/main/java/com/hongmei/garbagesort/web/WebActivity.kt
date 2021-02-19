package com.hongmei.garbagesort.web

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.initClose
import com.hongmei.garbagesort.widget.WebLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.web_activity.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class WebActivity : BaseActivity<BaseViewModel>() {

    private var webView: AgentWeb? = null

    override fun layoutId() = R.layout.web_activity

    override fun initView(savedInstanceState: Bundle?) {
        val url = intent.getStringExtra(EXTRA_URL)
        webView = AgentWeb.with(this)
            .setAgentWebParent(rootLayout, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator()
            .setWebChromeClient(mWebChromeClient)
            .setWebViewClient(mWebViewClient)
            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setWebLayout(WebLayout(this))
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
            .createAgentWeb()
            .ready()
            .go(url)
    }

    private val mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        }
    }
    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            toolbar?.initClose(title) {
                finish()
            }
        }
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"

        fun start(activity: Activity, url: String) {
            val intent = Intent(activity, WebActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            activity.startActivity(intent)
        }
    }
}