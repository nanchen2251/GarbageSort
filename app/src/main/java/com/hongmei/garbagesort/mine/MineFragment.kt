package com.hongmei.garbagesort.mine

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ColorUtils.getColor
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.ext.toastNormal
import com.hongmei.garbagesort.login.LoginActivity
import com.hongmei.garbagesort.util.CacheUtil
import kotlinx.android.synthetic.main.mine_fragment.*

/**
 * Date: 2021-02-02
 * Desc: 我的页面
 */
class MineFragment : BaseFragment<MineViewModel>() {
    override fun layoutId(): Int {
        return R.layout.mine_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
        mineNickNameText.text = appViewModel.userinfo.value?.nickname ?: "-"
        mineHeader.setOnClickListener {
            toastNormal("暂不支持修改个人资料")
        }
        mineZoomView.initView(mineHeader, bgView, mineEntranceLayout)
        initClickListeners()
    }

    private fun initClickListeners() {
        mineScoreLayout.setOnClickListener {
            toastNormal("我的积分功能暂未开通...")
        }
        mineFavoriteLayout.setOnClickListener {
            toastNormal("我的收藏功能暂未开通")
        }
        mineAboutLayout.setOnClickListener {

        }
        mineSettingLayout.setOnClickListener {

        }
        mineLogoutLayout.setOnClickListener {
            val dialogBuilder = NiftyDialogBuilder.getInstance(context)
            dialogBuilder
                .withMessage("你确定要退出登录么?")
                .withTitle(null)
                .withButton1Text("确定")
                .withButton2Text("取消")
                .withDialogColor(getColor(R.color.blue))
                .isCancelableOnTouchOutside(false)
                .setButton1Click {
                    appViewModel.userinfo.value = null
                    CacheUtil.setUser(appViewModel.userinfo.value)
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }
                .setButton2Click {
                    dialogBuilder.dismiss()
                }
                .show()
        }
    }

}