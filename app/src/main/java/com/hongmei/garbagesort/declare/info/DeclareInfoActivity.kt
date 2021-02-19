package com.hongmei.garbagesort.declare.info

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.declare.DeclareInfo
import com.hongmei.garbagesort.ext.initClose
import com.hongmei.garbagesort.ext.toastNormal
import com.hongmei.garbagesort.util.GsonFactory
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.declare_info_activity.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.io.File
import java.util.*

/**
 * 信息申报发布页面
 */
class DeclareInfoActivity : BaseActivity<DeclareInfoViewModel>(), BGASortableNinePhotoLayout.Delegate {

    override fun layoutId(): Int {
        return R.layout.declare_info_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            toolbar.setBackgroundColor(it)
        }
        toolbar.initClose("信息申报") {
            finish()
        }
        declareInfoPhotoLayout.setDelegate(this)

        declareInfoSelectPhoto.setOnClickListener {
            choicePhotoWrapper()
        }

        // 点击发布
        declareInfoPublishBtn.setOnClickListener {
            val content: String = declareInfoEdit.text.toString().trim { it <= ' ' }
            if (content.isEmpty()) {
                toastNormal("必须输入申报内容！")
                return@setOnClickListener
            }
            val declareInfo = DeclareInfo(content, declareInfoPhotoLayout.data)
            val jsonString = GsonFactory.toJson(declareInfo)
            val intent = Intent()
            intent.putExtra(EXTRA_MOMENT, declareInfo)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onClickNinePhotoItem(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: ArrayList<String>?
    ) {
        val photoPickerPreviewIntent = BGAPhotoPickerPreviewActivity.IntentBuilder(this)
            .previewPhotos(models) // 当前预览的图片路径集合
            .selectedPhotos(models) // 当前已选中的图片路径集合
            .maxChooseCount(declareInfoPhotoLayout.maxItemCount) // 图片选择张数的最大值
            .currentPosition(position) // 当前预览图片的索引
            .isFromTakePhoto(false) // 是否是拍完照后跳转过来
            .build()
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW)
    }

    override fun onClickAddNinePhotoItem(sortableNinePhotoLayout: BGASortableNinePhotoLayout?, view: View?, position: Int, models: ArrayList<String>?) {
        choicePhotoWrapper()
    }

    override fun onNinePhotoItemExchanged(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        fromPosition: Int,
        toPosition: Int,
        models: ArrayList<String>?
    ) {

    }

    override fun onClickDeleteNinePhotoItem(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: ArrayList<String>?
    ) {
        declareInfoPhotoLayout?.removeItem(position)
    }

    private fun choicePhotoWrapper() {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .subscribe {
                if (it) {
                    // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                    val takePhotoDir = File(Environment.getExternalStorageDirectory(), "garbagesort")
                    val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(this)
                        .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                        .maxChooseCount(declareInfoPhotoLayout.maxItemCount - declareInfoPhotoLayout.itemCount) // 图片选择张数的最大值
                        .selectedPhotos(null) // 当前已选中的图片路径集合
                        .pauseOnScroll(true) // 滚动列表时是否暂停加载图片
                        .build()
                    startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO)
                } else {
                    toastNormal("请打开相机和存储权限后重试！")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            declareInfoPhotoLayout?.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data))
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            declareInfoPhotoLayout?.data = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data)
        }
    }

    companion object {
        private const val RC_CHOOSE_PHOTO = 1
        private const val RC_PHOTO_PREVIEW = 2
        const val EXTRA_MOMENT = "EXTRA_MOMENT"
    }

}