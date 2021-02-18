package com.hongmei.garbagesort.declare

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.declare.info.DeclareInfoActivity
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
        declareFab.setOnClickListener {
            // 点击信息申报页面
            startActivity(Intent(activity, DeclareInfoActivity::class.java))
        }
        // 信息展示
        declareRv.layoutManager = LinearLayoutManager(context)
        adapter = DeclareAdapter(declareRv, this)
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
                arrayListOf("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered1.png")
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报2张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered2.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered3.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报9张网络图片",
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
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报5张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报3张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered4.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered5.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered6.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报8张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报4张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered7.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered8.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered9.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered10.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报2张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered2.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered3.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报3张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered4.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered5.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered6.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报4张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered7.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered8.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered9.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered10.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报9张网络图片",
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
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报1张网络图片",
                arrayListOf("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered1.png")
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报5张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报6张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报7张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png"
                )
            )
        )
        infoList.add(
            DeclareInfo(
                "测试申报信息，上报8张网络图片",
                arrayListOf(
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered11.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered12.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered13.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered14.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered15.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered16.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered17.png",
                    "http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered18.png"
                )
            )
        )
        val photos = ArrayList<String>()
        for (i in 1..18) {
            photos.add("http://bgashare.bingoogolapple.cn/refreshlayout/images/staggered$i.png")
        }
        infoList.add(DeclareInfo("测试申报信息，上报18张网络图片", photos))
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


}