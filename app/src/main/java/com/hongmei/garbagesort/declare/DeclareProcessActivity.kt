package com.hongmei.garbagesort.declare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.initClose
import com.hongmei.garbagesort.ext.visible
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.declare_item.*
import kotlinx.android.synthetic.main.declare_process_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class DeclareProcessActivity : BaseActivity<BaseViewModel>(), BGANinePhotoLayout.Delegate {
    private var currentPhotoLayout: BGANinePhotoLayout? = null

    override fun layoutId(): Int {
        return R.layout.declare_process_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            toolbar.setBackgroundColor(it)
            submitBtn.modifyAttribute(fillColor = it)
        }
        toolbar.initClose("信息处理") { finish() }
        val model = intent.getParcelableExtra<DeclareInfo>(MODEL) ?: return
        if (model.content.isEmpty()) {
            declare_content.visible(false)
        } else {
            declare_content.visible(true)
            declare_content.text = model.content
        }
        // 图片处理
        currentPhotoLayout = declare_photos
        currentPhotoLayout?.setDelegate(this)
        currentPhotoLayout?.data = model.photos
        declare_address.text = model.address
        // 处理页面不需要看到是否已处理的标签
        declare_image_done.visible(false)
        declare_username.text = model.name
        submitBtn.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    override fun onClickNinePhotoItem(ninePhotoLayout: BGANinePhotoLayout?, view: View?, position: Int, model: String?, models: MutableList<String>?) {
        currentPhotoLayout = ninePhotoLayout
        photoPreviewWrapper()
    }

    override fun onClickExpand(ninePhotoLayout: BGANinePhotoLayout?, view: View?, position: Int, model: String?, models: MutableList<String>?) {
        ninePhotoLayout?.setIsExpand(true)
        ninePhotoLayout?.flushItems()
    }

    private fun photoPreviewWrapper() {
        currentPhotoLayout?.let { currentPhotoLayout ->
            RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(this)
                        if (currentPhotoLayout.itemCount == 1) {
                            // 预览单张图片
                            photoPreviewIntentBuilder.previewPhoto(currentPhotoLayout.currentClickItem)
                        } else if (currentPhotoLayout.itemCount > 1) {
                            // 预览多张图片
                            photoPreviewIntentBuilder.previewPhotos(currentPhotoLayout.data)
                                .currentPosition(currentPhotoLayout.currentClickItemPosition) // 当前预览图片的索引
                        }
                        startActivity(photoPreviewIntentBuilder.build())
                    }
                }
        }
    }

    companion object {
        private const val MODEL = "model"
        fun startActivityForResult(fragment: DeclareFragment, model: DeclareInfo, requestCode: Int) {
            val intent = Intent(fragment.activity, DeclareProcessActivity::class.java).apply {
                putExtra(MODEL, model)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

}