package com.hongmei.garbagesort.mine

import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.color.colorChooser
import com.hongmei.garbagesort.GlobalData
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.bean.UserType
import com.hongmei.garbagesort.ext.showMessage
import com.hongmei.garbagesort.ext.toastNormal
import com.hongmei.garbagesort.ext.visible
import com.hongmei.garbagesort.login.LoginActivity
import com.hongmei.garbagesort.mine.about.AboutActivity
import com.hongmei.garbagesort.util.CacheDataManager
import com.hongmei.garbagesort.util.CacheUtil
import com.hongmei.garbagesort.util.ColorUtil
import com.hongmei.garbagesort.util.SettingUtil
import com.hongmei.garbagesort.widget.shape.ShapeConstraintLayout
import kotlinx.android.synthetic.main.mine_fragment.*

/**
 * Date: 2021-02-02
 * Desc: 我的页面
 */
class MineFragment : BaseFragment<MineViewModel>() {
    override fun layoutId(): Int {
        return R.layout.mine_fragment
    }

    private var mineHeaderLayout: ShapeConstraintLayout? = null

    override fun initView(savedInstanceState: Bundle?) {
        mineNickNameText.text = appViewModel.userinfo.value?.nickname ?: "-"
        mineTipsText.text = when(appViewModel.userinfo.value?.type){
            UserType.GOVERNMENT -> "政府人员"
            UserType.EXECUTOR -> "这行人员"
            UserType.GENERAL -> "普通用户"
            UserType.SUPERVISOR -> "监察人员"
            else -> "查看并编辑个人资料"
        }
        mineHeader.setOnClickListener {
            toastNormal("暂不支持修改个人资料")
        }
        mineHeaderLayout = mineHeader
        appViewModel.appColor.value?.let {
            mineHeader.modifyAttribute(fillColor = it)
        }
        mineZoomView.initView(mineHeader, bgView, mineEntranceLayout)
        context?.let {
            mineCacheText.text = CacheDataManager.getTotalCacheSize(it)
        }
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
            startActivity(Intent(activity, AboutActivity::class.java))
        }
        mineSettingLayout.setOnClickListener {
            activity?.let { activity ->
                MaterialDialog(activity).show {
                    title(R.string.choose_theme_color)
                    colorChooser(
                        ColorUtil.ACCENT_COLORS,
                        initialSelection = SettingUtil.getColor(activity),
                        subColors = ColorUtil.PRIMARY_COLORS_SUB
                    ) { _, color ->
                        ///修改颜色
                        SettingUtil.setColor(activity, color)
                        mineHeaderLayout?.modifyAttribute(fillColor = color)
                        //通知其他界面立马修改配置
                        appViewModel.appColor.value = color
                    }
                    getActionButton(WhichButton.POSITIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                    positiveButton(R.string.done)
                    negativeButton(R.string.cancel)
                }
            }
        }
        mineClearCacheLayout.setOnClickListener {
            showMessage(
                "确定清除缓存么？",
                positiveButtonText = "确定",
                negativeButtonText = "取消",
                positiveAction = {
                    CacheDataManager.clearAllCache(activity)
                    mineCacheText.text = ""
                })
        }
        mineLogoutLayout.setOnClickListener {
            showMessage(
                "确定退出登录吗",
                positiveButtonText = "退出",
                negativeButtonText = "取消",
                positiveAction = {
                    CacheUtil.setUser(null)
                    appViewModel.userinfo.value = null
                    GlobalData.currentLocation = null
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                })
        }
        mineMsgLayout.visible(
            appViewModel.userinfo.value?.type == UserType.EXECUTOR
                    || appViewModel.userinfo.value?.type == UserType.GOVERNMENT
                    || appViewModel.userinfo.value?.type == UserType.SUPERVISOR
        )
        mineMsgLayout.setOnClickListener {
            toastNormal("暂未开通...")
        }
    }

}