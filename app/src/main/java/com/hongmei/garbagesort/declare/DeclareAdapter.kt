package com.hongmei.garbagesort.declare

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.bean.UserType

/**
 * Date: 2021-02-18
 * Desc:
 */
class DeclareAdapter(
    recyclerView: RecyclerView,
    private val delegate: BGANinePhotoLayout.Delegate,
    private val userType: Int
) : BGARecyclerViewAdapter<DeclareInfo>(recyclerView, R.layout.declare_item) {

    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        super.setItemChildListener(helper, viewType)
        helper?.setItemChildClickListener(R.id.declare_delete)
    }

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
        helper.setText(R.id.declare_username, model.name)
        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.declare_photos)
        ninePhotoLayout.setDelegate(delegate)
        ninePhotoLayout.data = model.photos
        helper.setText(R.id.declare_address, model.address)
        when (userType) {
            UserType.GENERAL -> {
                // 普通人员看不到待处理和已处理
                helper.setVisibility(R.id.declare_image_done, View.GONE)
            }
            UserType.EXECUTOR -> {
                // 执行人员可以看到待处理和已处理
                helper.setVisibility(R.id.declare_image_done, View.VISIBLE)
                helper.setImageResource(
                    R.id.declare_image_done,
                    if (model.done) R.drawable.img_done else R.drawable.img_need_do
                )
            }
            else -> {
                // 其他人员只可以看到已处理
                if (model.done) {
                    helper.setVisibility(R.id.declare_image_done, View.VISIBLE)
                    helper.setImageResource(R.id.declare_image_done, R.drawable.img_done)
                } else {
                    helper.setVisibility(R.id.declare_image_done, View.GONE)
                }
            }
        }
        when (userType) {
            UserType.EXECUTOR, UserType.SUPERVISOR -> {
                // 监察和执行人员可以看到删除和处理
                helper.setVisibility(R.id.declare_delete, View.VISIBLE)
            }
            else -> {
                helper.setVisibility(R.id.declare_delete, View.GONE)
            }
        }
    }
}