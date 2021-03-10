package com.hongmei.garbagesort.declare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.bean.UserType
import com.hongmei.garbagesort.declare.info.DeclareInfoActivity
import com.hongmei.garbagesort.ext.showMessage
import com.hongmei.garbagesort.ext.toastNormal
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
    private var currentClickPosition = -1

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
        adapter =
            DeclareAdapter(declareRv, this, appViewModel.userinfo.value?.type ?: UserType.GENERAL)
        declareRv.adapter = adapter
        declareRv.addOnScrollListener(BGARVOnScrollListener(activity))

        showLoading()
        declareRv.postDelayed({
            addNetImageTestData()
            dismissLoading()
        }, 1500)

        if (appViewModel.userinfo.value?.type == UserType.EXECUTOR) {
            adapter?.setOnRVItemClickListener(object : BGAOnRVItemClickListener {
                override fun onRVItemClick(parent: ViewGroup?, itemView: View?, position: Int) {
                    // 只有未处理的才可以点击
                    val model = adapter?.data?.get(position) ?: return
                    if (model.done) {
                        // 已处理的直接提示
                        toastNormal("本条信息已处理，仅未处理可以点击")
                    } else {
                        currentClickPosition = position
                        DeclareProcessActivity.startActivityForResult(
                            this@DeclareFragment,
                            model,
                            REQUEST_CODE_PROCESS
                        )
                    }
                }
            })
        }
        adapter?.setOnItemChildClickListener { _, childView, position ->
            if (childView?.id == R.id.declare_delete) {
                showMessage("确定要删除这条内容吗？", positiveAction = {
                    adapter?.data?.removeAt(position)
                    adapter?.notifyItemRemoved(position)
                }, negativeButtonText = "取消")
            }
        }
    }

    /**
     * 添加网络图片测试数据
     */
    private fun addNetImageTestData() {
        val infoList: MutableList<DeclareInfo> = ArrayList()
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，未分类收集",
                "云路裕庭",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcWlTBQtInMHOge31rRUvYc5.forp5G0N2A9TZOvimDCAI4nqk58KMGczdg*EekensZ3llUY1XWaIKobL3F4TC84!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcWlTBQtInMHOge31rRUvYc6q3KS1KwAbdhlAfvn7RGwlSKR2GeanSSLAmuIUxFdSPKQSdVOPk20QrBKCoPIUqQ0!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcTeW3Mw2l9tD9xwtNl210WclNdxdwzU6SokdOYN268JNwFhV2IHF2dkEqZO4KdIXb7SiPNxgCmsVISOFAwFQRDw!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4"
                ),
                false
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏",
                "高新区税务局",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcY8krtlzerGA.hWnC.keSLzNjbpZ1C4HZ4iVsFoI8MarXZQPhBbjSS9IiF6MBQCf8dGJuKuGOBgnVv9K8uQUiAI!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcY8krtlzerGA.hWnC.keSLwoBCg25*3LoouMr3WtHaUO6GCGCmIVibEtoJrnstiNpt4Qo8gSfJu4AdVmEEy2wCw!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcY8krtlzerGA.hWnC.keSLxPsJqu7Xe0LmSokr9wrwYIk10dZQ5aCCPNcFPJn6P5Jcp04QpgA8Y0h.jgAYt*e2g!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcWlTBQtInMHOge31rRUvYc7Io2PBr8iAKtcznkeB9LxFyz7OAEBYXwFye2bLxB9qEh6g377rcVnQVAFZL7jZlAU!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcWlTBQtInMHOge31rRUvYc5F6LyamP26Rgm4Q*ghWOxlqdWjyFCq8oPDZFV4VwAYnDRhxqnu578dUUU0EipIsIk!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未分类收集，摆放不整齐",
                "中信银行昆明白塔路支行停车场",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcY8krtlzerGA.hWnC.keSLwwdceOgKAkSGWBpEBASIwbbhe7FH0vF*P5Y0QaJtEhOepYX3qems7K0GOx76ezm1s!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4"
                ), false
            )
        )
        infoList.add(
            DeclareInfo(
                "未按规定投放四色桶",
                "昆明市呈贡区应急管理局",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcfaivZqdw9fbECkSXqsmYP13Xp4ELQyFHxIy02GLu.GvwafW9YMCyIwbkt*x4YAkZTYBdCWKNLoQVfVXFlYOYzo!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcfaivZqdw9fbECkSXqsmYP2awLHlr5f9GZee1ct2VIPDtSmmFUMTzX5N*hViMChtSmI1kMmxBraUoHvMzoGh2pA!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcfaivZqdw9fbECkSXqsmYP1BbR3lHcISYnSER7Fpe9jcE.s99L22CB6EHNUSN.yBA0ZfTLsQFcg.KXhWlwVoO7U!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未分类收集",
                "东寺塔茶花园",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcfaivZqdw9fbECkSXqsmYP0BfGk36ise98CXK.ulBrPZ3xAcq3O0kibjlwof.0TmqbD3vrINWyxrZMLXswqqBs8!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未分类收集,分类桶未密闭",
                "巡津街小区",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcQ6lxOTOcZrgp.2YfMYBhcHAJ43jDYDZWCrFFx7wspYo4zNPASuIdekfjS4rp2PHP5gT4z8CaMC3RYf2WLia62k!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcQ6lxOTOcZrgp.2YfMYBhcGh0dCjet6n2aH7qSlyXcPw*lATid1dXC6zqhl*m04vBPARUm4ZfmQptkxY6dwwWRA!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcQ6lxOTOcZrgp.2YfMYBhcHg.38jh1W0ffWW4FO4mE5jx*G5abhD0sXgGtRprgK.2FdYi2HPxpo*0vQXzr6xlCM!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未分类收集,分类桶未密闭",
                "篆塘法制公园",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSHX5c1jhtFTm9.69f1HJ1KtEQmzar5f4jJNlMQbJTm8e7smFzGngU52oae30RC27.9J4BBXp4LzcajxTrKTqB8!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSHX5c1jhtFTm9.69f1HJ1J*orCElK6.wOgczVqJRKrw9XjIIMcS2.RgBMo6ko7CekMoCJqMdkuLnasVfs4D3CE!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/ruAMsa53pVQWN7FLK88i5nOxr0*LYsCqwym1MWbyvB696kNSFrVKARymMtLDEwp4kUY7oXhmsOxrCe*yDGmWXAc*7PdsJnKKsnUhpnqndG4!/b&bo=oAU4BAAAAAABB7k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSHX5c1jhtFTm9.69f1HJ1KkcTp3e.zOHBEmNT8WxDKRDBLAZzDolFgHco25*x.x1zOilMalVpacAhNU7xSFTpc!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcQ6lxOTOcZrgp.2YfMYBhcF1h.Io5hJiBfILJPds1fbjADzOofyPYt5S8xBLmsUxUIpSc*aoyxG3J.98FYp.2pQ!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，外观颜色不符合标准，未正确粘贴分类标识，未分类收集，分类桶损坏，分类桶未密闭",
                "云南地质调查队小区",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcaGWhNwgVl9ojhby.egxygW95NZ7Bgvn7EjnkqodKQiHX8o8mygjbGcM00Nmug5ziW3CU6U0CnJ0TK*Twbs4TrU!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcaGWhNwgVl9ojhby.egxygVuUdenPtRkQvpXbHq4jZu3.37JBfeU7K0cpBTLPwWQZyGKEr2uyhvaEBYQzLbz*dM!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcaGWhNwgVl9ojhby.egxygXs9pD1sU*cvO4VRQdKebqEBT9VTVSRGan3UBD9ML.eSB1Kiu5nCvfk*f*apUVqkEE!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcaGWhNwgVl9ojhby.egxygUjRF0bXxmPEc5WWuXCMunnxxsAk26jA18TjuwRRWKlr80bOQS6iMzrSe6ljF55Rns!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未分类收集，分类桶损坏，摆放不整齐",
                "国际银座",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVMH7PhfAXySL5ozyNJ6ShTVMLiM06L6Y*4PKwEP7xtB8PLWAS1F7XjArDOoixfZsQx*78kfqEltAdcuD0M*XFU!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVMH7PhfAXySL5ozyNJ6ShSz71Yyh2HAprOd0Oac.442A0aWhGJzKQQXyHh5eQnLej3XF6w66p5WmgQeqYdruL4!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVMH7PhfAXySL5ozyNJ6ShS3XDzNzuTskv71OkT7P4FF.WCGbSAqIoCMIw1jERCQs8iH4G1qJP.tMB9*Hf3yAIY!/b&bo=nwU4BAAAAAABN7Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVMH7PhfAXySL5ozyNJ6ShSrlsGETK*6x3ltVkvipCIiNPobESzWgZji3CNfrs8O7GtKGuFzpx8Fk*C7A9hbrgM!/b&bo=nwU4BAAAAAABF5Y!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，未分类收集，摆放不整齐",
                "建工新城雅澜庭",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcS0*q0PgGSmwZ12chrsdNaIVhPTmnnkfllVajhq*O9BKUf0lQ9QDjCH9jAp1Ge7wylgXychAxGL9cryPt84zFR0!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcS0*q0PgGSmwZ12chrsdNaJ0DfQyWjLuIl6chaX00*3jf9or*n1o35Jd5m6.YDvyMC4xD9jPMoA*qz2hpkZJ2pA!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放指示牌",
                "胜利广场",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcS0*q0PgGSmwZ12chrsdNaJm5ZyDLWSG7ehdEtFMm.JMCrrjn1Rc4te1ZadCeK79kpfruZQ2h97R*CrQKP4gOP8!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcS0*q0PgGSmwZ12chrsdNaLasWbXX.mSHDYGUeoJQ7Ic4TqYmSBeWR8nRcfcJnfnc9qqsZPPAMM.6I4LD9QrWVU!/b&bo=oAU4BAAAAAABN4k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放指示牌，未分类收集",
                "公园1903",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/ruAMsa53pVQWN7FLK88i5gqUwTbDQylMRHrAS7fIVfIR9j7uXBPKqxxTMk5MC6ZTPI6YE*DRdWCz37aa9iPWSdkTUiI.g.mcZXuo6cuy0bw!/b&bo=oAU4BAAAAAABB7k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqACifyLKA7MQ3Aan5sJ2jbm8dc9lD5SQvOlc65uhr5cjnue6wu*BkI1rnEkBbfaPFpgK26LGfYbHg*E1wRk20!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqACifyLKA7MQ3Aan5sJ2gABtwgtJDjOsZQM4jAxPFvYqPD8nRFU5Pki4M*FNhkNXjqgzKILbzUpsUFGBYQKzI!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqACifyLKA7MQ3Aan5sJ2gABtwgtJDjOsZQM4jAxPFvYqPD8nRFU5Pki4M*FNhkNXjqgzKILbzUpsUFGBYQKzI!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，未正确粘贴分类标识，未分类收集，分类桶未密闭",
                "滇池康城",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqgJIgcz8o23FCoMa7ENBQRMEC70GoSeZiJGraATCorEcxz0DLHTkpTgXA.TbEAN3xH4njmf.v04kERLzpR8.o!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqACifyLKA7MQ3Aan5sJ2jZY0Rk6lzD2BNf136s0KPBsbrTVS87Q4qh3eg63h2XQg7GyPGvzGu3vj*TqM3a3bo!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放指示牌，未正确粘贴分类标识",
                "金格奥特莱斯",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqgJIgcz8o23FCoMa7ENBTnuSp87QWOihlxQ6hylU.3waCJi3lm6QEXEAL8EX4WbdUfoqPxo6K.Br925QoShaM!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqgJIgcz8o23FCoMa7ENBTG8uzGlKoXO9JghL5zOvdNSvYd.2XzkQr6S5OCoKxwz8ebjpABw5TnghG0AyM*7rw!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mccqgJIgcz8o23FCoMa7ENBTeZJQ5hq.lwV3Re02T6VczojvOnmRE5.8aecNsQusJSDwF8hOuRtkkkf9V*a*Sv4U!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，未正确粘贴分类标识，未分类收集，分类桶未密闭",
                "螺蛳湾公交枢纽站",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbDSt*FZrBk5IhQyUmfEaeFQsldCSwcDEOJjjLpsZy5M49Ln.ChcgfJmmgryJtlYwDbmb0x9DMaZZ1wQFr5hTKM!/b&bo=nwU4BAAAAAABF5Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbDSt*FZrBk5IhQyUmfEaeFAN*jeVEOHYRci*LImain*okxTDzXBTFgggGtyAAJZLo72pzxrOdNnvYT6M9TbL50!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放公示栏，无投放指示牌，未按规定投放四色桶，未正确粘贴分类标识，未分类收集，分类桶损坏，分类桶未密闭，摆放不整齐",
                "东峻集团",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSxwVb9B*ry8oioyjFH6T46LQKsOQxBWxHVvRW1P9mqHJ.I6QE*F9NfKn9690X0aumnhajkRfTpa6h75lgK0zP0!/b&bo=nwU4BAAAAAABJ6Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbDSt*FZrBk5IhQyUmfEaeE2AGFe2jonO1rvVbcKw25*R9RO7JFOUrYtGux9Xdco6mefQ5ExgrYiDz6Zw8R8Gq8!/b&bo=nwU4BAAAAAABF5Y!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbDSt*FZrBk5IhQyUmfEaeEqn56tiTYYCz9iLHnPgZYsGBARhJYBltsgQGzPXlAwN19L1jAG0ha8*6J*kzd6Nf4!/b&bo=nwU4BAAAAAABF5Y!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "未分类收集，分类桶未密闭",
                "后新街小区",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSxwVb9B*ry8oioyjFH6T458bE4iGwYOtzj5LAQc3BlvwD9P5lloJsurY5hwXcC4EFx3y9UnXdV6HHKWFAj0ams!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSxwVb9B*ry8oioyjFH6T45Wllukbir8DT1LmcAlXJp83Dqk.NjlEdtNBQzkZVdMAEhZJW1ZA2y17sVNyA3vr7Y!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcSxwVb9B*ry8oioyjFH6T47ImIV8.jRWmmMd89ZpVMI1nbsWmEPXXQXfc1SANFL6bvF1XVZG4TVwMInD**oUcsY!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放指示牌",
                "茶花公园",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVrHL9o702E4QkeTvHvv9T9HFeuNQ02DkTrbq4KaW8lKhbi4jFAPICgFLPQG2MdVU59d5gtQEXkYJCN5bcgqNSc!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVrHL9o702E4QkeTvHvv9T9ghWEFAHd.5*KoqjMWK3rbLDR8uSkQ4ZxredqqwTswIa61SL4.y7Y0uZA3r*pPUZc!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVrHL9o702E4QkeTvHvv9T*UCcQRwz6WSEogcVMCurolAhFxf8vIzkDHBzv7gTdRYdCBfbRD26eWFdEx.HImJ9g!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "无投放指示牌",
                "云南民族村广场",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcUxi9XAHNr6cJdx1SzxslbWRJSv8fAn.AGxHWwzXlYUOiSZdej13pxRfKnOJPGLyinehf44yOgnYN*DcDJoVjuY!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcVrHL9o702E4QkeTvHvv9T*0HrzAKfFJThYrxlUtu4KNpGTShs3tEhxD*wmrsPHN7pnrzBAYaRbqupABC*i9Lzo!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4"
                ), true
            )
        )
        infoList.add(
            DeclareInfo(
                "未分类收集，分类桶未密闭，摆放不整齐",
                "昆明市呈贡区审计局",
                arrayListOf(
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbxeZ7YoDkiIFky5y.pzUK6DdhDf8.oxl.7buiceTbdFr*btTIfyrAHoVspAD4MpKRFE6caZ6jFfBEunTqsNk0w!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcbxeZ7YoDkiIFky5y.pzUK5KfPTEuDMfeMY3Ps9NAIy14qlKG2wrqtvOIG7ga5TvYkJNItFg33YarIHrnyOBmfc!/b&bo=oAU4BAAAAAABF6k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcUxi9XAHNr6cJdx1SzxslbUBZ6jPrstDPhbxW2Zzi5tYuNkqJS9sBbuE8tVX1vIDfyXV3IbUp2NuqHKlFSIRcm8!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcUxi9XAHNr6cJdx1SzxslbXX.47Cs3c043sDeaZkiZhOETDEvCnYHgFKXp4DeivLPuzZknaRNwru0k0bjSFMzVs!/b&bo=oAU4BAAAAAABJ5k!&rf=viewer_4",
                    "http://m.qpic.cn/psc?/V546uaiN3WTFLG2vp4Cm0byXzu2SNWOD/45NBuzDIW489QBoVep5mcUxi9XAHNr6cJdx1SzxslbXBrd6cXejOaToKyKx51S0jjMUedYJC69Q4LPtzi1rmIcIT3ajwo.BZ*c6OQNGUHPg!/b&bo=oAU4BAAAAAABN4k!&rf=viewer_4"
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


    override fun onClickNinePhotoItem(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
        currentPhotoLayout = ninePhotoLayout
        photoPreviewWrapper()
    }

    private fun photoPreviewWrapper() {
        currentPhotoLayout?.let { currentPhotoLayout ->
            RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        val photoPreviewIntentBuilder =
                            BGAPhotoPreviewActivity.IntentBuilder(activity)
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

    override fun onClickExpand(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PROCESS) {
            val infoList = adapter?.data ?: return
            if (currentClickPosition >= 0 && currentClickPosition < infoList.size) {
                infoList[currentClickPosition].done = true
                adapter?.notifyDataSetChanged()
            }
        }
    }

    companion object {
        private const val RC_ADD_MOMENT = 1
        private const val REQUEST_CODE_PROCESS = 2
    }

}