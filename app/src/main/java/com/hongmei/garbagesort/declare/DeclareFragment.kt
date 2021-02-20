package com.hongmei.garbagesort.declare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.bean.UserType
import com.hongmei.garbagesort.declare.info.DeclareInfoActivity
import com.hongmei.garbagesort.ext.visible
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.declare_fragment.*
import java.util.*

/**
 * Date: 2021-02-12
 * Desc: 信息申报展示页面
 */
class DeclareFragment : BaseFragment<DeclareViewModel>(), BGANinePhotoLayout.Delegate {

    private var adapter: DeclareAdapter? = null
    private var currentPhotoLayout: BGANinePhotoLayout? = null

    override fun layoutId(): Int {
        return R.layout.declare_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 只有监察用户才可以申报
        declareFab.visible(appViewModel.userinfo.value?.type == UserType.SUPERVISOR)
        declareFab.setOnClickListener {
            // 点击信息申报页面
            startActivityForResult(Intent(activity, DeclareInfoActivity::class.java), RC_ADD_MOMENT)
        }
        // 信息展示
        declareRv.layoutManager = LinearLayoutManager(context)
        // 除了执行人员都可以看到是否已处理的状态
        val isExecutor = appViewModel.userinfo.value?.type == UserType.EXECUTOR
        adapter = DeclareAdapter(declareRv, this, isExecutor)
        declareRv.adapter = adapter
        declareRv.addOnScrollListener(BGARVOnScrollListener(activity))

        showLoading()
        declareRv.postDelayed({
            addNetImageTestData()
            dismissLoading()
        }, 1500)

    }

    /**
     * 添加网络图片测试数据
     */
    private fun addNetImageTestData() {
        val infoList: MutableList<DeclareInfo> = ArrayList()
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报1张网络图片",
                "昆明市政府",
                arrayListOf("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered1.png"),
                false
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报2张网络图片",
                "云南大学",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered2.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered3.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报9张网络图片",
                "昆明翠湖公园",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered19.png"
                ), false
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报5张网络图片",
                "滇池",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报3张网络图片",
                "金马碧鸡坊",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered4.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered5.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered6.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报8张网络图片",
                "南屏步行街",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报4张网络图片",
                "云大会泽院仰止楼",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered7.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered8.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered9.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered10.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报2张网络图片",
                "石林风景区",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered2.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered3.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报3张网络图片",
                "海埂大坝",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered4.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered5.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered6.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报4张网络图片",
                "龙门",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered7.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered8.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered9.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered10.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报9张网络图片",
                "云南陆军讲武堂旧址",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered19.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报1张网络图片",
                "昆明动物园",
                arrayListOf("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered1.png"), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报5张网络图片",
                "昆明植物园",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报6张网络图片",
                "官渡古镇",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报7张网络图片",
                "云南省博物馆",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报8张网络图片",
                "云南野生动物园",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png"
                ), true
            )
        )
        val photos = ArrayList<String>()
        for (i in 1..18) {
            photos.add("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered$i.png")
        }
        infoList.add(DeclareInfo("测试申报信息，上报18张网络图片", "黑龙潭公园", photos, true))
        adapter?.data = infoList
    }


    override fun onClickNinePhotoItem(ninePhotoLayout: BGANinePhotoLayout?, view: View?, position: Int, model: String?, models: MutableList<String>?) {
        currentPhotoLayout = ninePhotoLayout
        photoPreviewWrapper()
    }

    private fun photoPreviewWrapper() {
        currentPhotoLayout?.let { currentPhotoLayout ->
            RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(activity)
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

    override fun onClickExpand(ninePhotoLayout: BGANinePhotoLayout?, view: View?, position: Int, model: String?, models: MutableList<String>?) {
        ninePhotoLayout?.setIsExpand(true)
        ninePhotoLayout?.flushItems()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == RC_ADD_MOMENT) {
            adapter?.addFirstItem(data.getParcelableExtra(DeclareInfoActivity.EXTRA_MOMENT))
            declareRv?.smoothScrollToPosition(0)
        }
    }

    companion object {
        private const val RC_ADD_MOMENT = 1
    }

}