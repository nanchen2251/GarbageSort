package com.hongmei.garbagesort.info

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import com.hongmei.garbagesort.R

/**
 * Date: 2021-02-18
 * Desc:
 */
class InfoAdapter(recyclerView: RecyclerView) :
    BGARecyclerViewAdapter<InfoEntity>(recyclerView, R.layout.item_category) {

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: InfoEntity?) {
        if (helper == null || model == null) {
            return
        }
        helper.setText(R.id.category_item_desc, model.content)
        helper.setVisibility(R.id.category_item_time, View.GONE)
        helper.setText(R.id.category_item_author, model.time)
    }
}