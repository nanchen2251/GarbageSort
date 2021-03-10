package com.hongmei.garbagesort.info

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hongmei.garbagesort.GlideImageLoader
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.bean.UserType
import com.hongmei.garbagesort.ext.visible
import com.hongmei.garbagesort.web.WebActivity
import com.zlylib.fileselectorlib.FileSelector
import kotlinx.android.synthetic.main.info_fragment.*


/**
 * Date: 2021-02-18
 * Desc: 信息公开页面
 */
class InfoFragment : BaseFragment<InfoViewModel>() {

    private var adapter: InfoAdapter? = null
    private val infoList = ArrayList<InfoEntity>()

    override fun layoutId(): Int {
        return R.layout.info_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
        val imageList = arrayListOf(
            "https://mmbiz.qpic.cn/mmbiz_jpg/GRfb58egMb54TRWKnzibGumFcbozk1a4o8yORwOjmpDQ2VJP7Vickib0ia1SI6f35RKTnyXDYDFEEXvpeCkZnLxFow/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1",
            "https://mmbiz.qpic.cn/mmbiz_png/GrVunCdSUianWJ41Q5g6AolIwAqQY8ZAksW53RA6ep2e6vl3ZEiaLdKmlcNv6dKME1wEoTzEnYGjrUUQPYLHKXPg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1",
            "https://mmbiz.qpic.cn/mmbiz_jpg/GRfb58egMb6mY38icVm7df7k1REy0vIzLv1SUJCr2RDz0OrvRFB2XZ8Zdib5oTySImUrm1bJ2CN4oK8udiaCxs88w/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"
        )

        infoBanner.setImages(imageList).setImageLoader(GlideImageLoader()).start()
        infoBanner.setOnBannerListener {
            activity?.run {
                val url = when (it) {
                    0 -> "https://mp.weixin.qq.com/s/_Mo0vlihNbZnt4zmCFZKuw"
                    1 -> "https://mp.weixin.qq.com/s/7hQ3PKA9CNcK0nXbY_gtmg"
                    2 -> "https://mp.weixin.qq.com/s/sGkhEBpZ3pc__GKrbkoajQ"
                    else -> "https://mp.weixin.qq.com/s/e3vvJ59BphLbO1DA39FjRA"
                }
                WebActivity.start(this, url)
            }
        }

        policyLayout1.setOnClickListener {
            activity?.run {
                WebActivity.start(this, "https://mp.weixin.qq.com/s/e3vvJ59BphLbO1DA39FjRA")
            }
        }
        policyLayout2.setOnClickListener {
            activity?.run {
                WebActivity.start(this, "https://mp.weixin.qq.com/s/j4c1MleM4PlE1hnc2BQMIQ")
            }
        }
        policyLayout3.setOnClickListener {
            activity?.run {
                WebActivity.start(this, "https://mp.weixin.qq.com/s/gIckDkam1wsCtB8594z9kg")
            }
        }

        // 处理简报部分
        infoRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = InfoAdapter(infoRecyclerView)
        adapter?.setOnRVItemClickListener { _, _, position ->
            activity?.run {
                BriefingActivity.start(this, infoList[position].time, infoList[position].pdfName)
            }
        }
        infoRecyclerView.adapter = adapter
        addTestData()


        // 政府管理人员展示上报监管简报的入口
        infoUploadBtn.visible(appViewModel.userinfo.value?.type == UserType.GOVERNMENT)
        infoUploadBtn.setOnClickListener {
            // 点击上报监管简报
            // 这里懒得适配这个库了，onActivityResult 放在 Activity 中了
            FileSelector.from(activity)
                .setMaxCount(5) //设置最大选择数
                .setFileTypes("png", "doc", "apk", "mp3", "gif", "txt", "mp4", "zip", "pdf") //设置文件类型
                .setSortType(FileSelector.BY_NAME_ASC) //设置名字排序
                .requestCode(REQUEST_CODE) //设置返回码
                .start()
        }
    }

    private fun addTestData() {
        infoList.clear()
        infoList.add(InfoEntity("2021年01月监管简报", "2021年01月", "202101.pdf"))
        infoList.add(InfoEntity("2020年12月监管简报", "2020年12月", "202012.pdf"))
        infoList.add(InfoEntity("2020年11月监管简报", "2020年11月", "202011.pdf"))
        infoList.add(InfoEntity("2020年10月监管简报", "2020年10月", "202010.pdf"))
        infoList.add(InfoEntity("2020年09月监管简报", "2020年09月", "202009.pdf"))
        infoList.add(InfoEntity("2020年08月监管简报", "2020年08月", "202008.pdf"))
        adapter?.data = infoList
    }

    companion object {
        const val REQUEST_CODE = 1
    }

}