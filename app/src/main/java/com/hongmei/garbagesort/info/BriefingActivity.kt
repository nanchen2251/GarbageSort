package com.hongmei.garbagesort.info

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.initClose
import kotlinx.android.synthetic.main.briefing_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.register_activity.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class BriefingActivity : BaseActivity<BaseViewModel>() {

    override fun layoutId(): Int {
        return R.layout.briefing_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            toolbar.setBackgroundColor(it)
        }
        val title = intent.getStringExtra(TITLE) ?: "监管报告"
        val pdfName = intent.getStringExtra(PDFNAME) ?: return
        toolbar.initClose(title) {
            finish()
        }
        pdfView.fromAsset(pdfName)
            .defaultPage(0)
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10) // in dp
            .load()
    }

    companion object {
        private const val TITLE = "title"
        private const val PDFNAME = "pdfName"

        fun start(activity: Activity, title: String, pdfName: String) {
            val intent = Intent(activity, BriefingActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(PDFNAME, pdfName)
            activity.startActivity(intent)
        }
    }
}