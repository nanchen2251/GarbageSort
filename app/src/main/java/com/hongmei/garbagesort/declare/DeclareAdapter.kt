package com.hongmei.garbagesort.declare

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R

/**
 * Date: 2021-02-18
 * Desc:
 */
class DeclareAdapter(recyclerView: RecyclerView, private val delegate: BGANinePhotoLayout.Delegate, private val isExecutor: Boolean) :
    BGARecyclerViewAdapter<DeclareInfo>(recyclerView, R.layout.declare_item) {

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: DeclareInfo?) {
        if (helper == null || model == null) {
            return
        }
        if (model.content.isEmpty()) {
            helper.setVisibility(R.id.declare_content, View.GONE)
        } else {
            helper.setVisibility(R.id.declare_content, View.VISIBLE)
            helper.setText(R.id.declare_content, model.content)
        }
        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.declare_photos)
        ninePhotoLayout.setDelegate(delegate)
        ninePhotoLayout.data = model.photos
        helper.setText(R.id.declare_address, model.address)
        helper.setVisibility(R.id.declare_image_done, if (model.done && isExecutor) View.VISIBLE else View.GONE)
    }
}