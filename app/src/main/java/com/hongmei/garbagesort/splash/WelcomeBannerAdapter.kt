package com.hongmei.garbagesort.splash

import android.view.View
import com.hongmei.garbagesort.R
import com.zhpan.bannerview.BaseBannerAdapter

class WelcomeBannerAdapter : BaseBannerAdapter<String, WelcomeBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.banner_itemwelcome
    }

    override fun createViewHolder(itemView: View, viewType: Int): WelcomeBannerViewHolder {
        return WelcomeBannerViewHolder(itemView);
    }

    override fun onBind(holder: WelcomeBannerViewHolder?, data: String?, position: Int, pageSize: Int) {
        holder?.bindData(data, position, pageSize);
    }
}
