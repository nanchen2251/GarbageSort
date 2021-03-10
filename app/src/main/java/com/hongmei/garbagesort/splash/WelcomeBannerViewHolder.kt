package com.hongmei.garbagesort.splash


import android.view.View
import android.widget.TextView
import com.hongmei.garbagesort.R
import com.zhpan.bannerview.BaseViewHolder

class WelcomeBannerViewHolder(view: View) : BaseViewHolder<String>(view) {
    override fun bindData(data: String?, position: Int, pageSize: Int) {
        val textView = findView<TextView>(R.id.bannerText)
        textView.text = data
    }

}
