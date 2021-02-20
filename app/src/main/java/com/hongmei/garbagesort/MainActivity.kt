package com.hongmei.garbagesort

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.declare.DeclareFragment
import com.hongmei.garbagesort.ext.toastNormal
import com.hongmei.garbagesort.ext.toastSuccess
import com.hongmei.garbagesort.info.InfoFragment
import com.hongmei.garbagesort.map.MapFragment
import com.hongmei.garbagesort.mine.MineFragment
import com.hongmei.garbagesort.search.SearchGarbageFragment
import com.hongmei.garbagesort.widget.AnnulusCustomizeView
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import com.zlylib.fileselectorlib.utils.Const
import kotlinx.android.synthetic.main.main_activity.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * APP 主页面
 */
class MainActivity : BaseActivity<BaseViewModel>() {
    /**
     * 上次点击返回键的时间
     */
    private var lastBackPressTime = -1L

    private val tabNames = arrayOf("地图", "申报", "公开", "我的")
    private val tabIcons = arrayOf(R.drawable.menu_tab_map, R.drawable.menu_tab_declare, R.drawable.menu_tab_info, R.drawable.menu_tab_me)
    private var supportFragmentTag = arrayOf(MAP_TAG, DECLARE_TAG, SEARCH_TAG, INFO_TAG, MINE_TAG)
    private var lastFragmentTag = ""
    private var circleProgressBar: AnnulusCustomizeView? = null
    private var loadingDialog: MaterialDialog? = null

    override fun layoutId(): Int {
        return R.layout.main_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // "内存重启"时调用
        if (savedInstanceState != null) {
            val fragmentList = supportFragmentManager.fragments
            val ft = supportFragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                ft.hide(fragment)
            }
            ft.commitAllowingStateLoss()
        }
        initTab(savedInstanceState)
    }

    private fun initTab(savedInstanceState: Bundle?) {
        mainTab.initWithSaveInstanceState(savedInstanceState)
        mainTab.showIconOnly()
        for (i in tabIcons.indices) {
            mainTab.addSpaceItem(SpaceItem(tabNames[i], tabIcons[i]))
        }
        mainTab.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                // 点击搜索
                switchToDestFragment(SEARCH_TAG)
            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {
                // 选中 Tab
                onTabItemSelected(itemName)
            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {
                // 选中 Tab
                onTabItemSelected(itemName)
            }

        })
        setDefaultTab()
    }

    private fun setDefaultTab() {
        switchToDestFragment(MAP_TAG)
    }

    private fun onTabItemSelected(tabName: String?) {
        when (tabName) {
            "地图" -> {
                switchToDestFragment(MAP_TAG)
            }
            "申报" -> {
                switchToDestFragment(DECLARE_TAG)
            }
            "公开" -> {
                switchToDestFragment(INFO_TAG)
            }
            "我的" -> {
                switchToDestFragment(MINE_TAG)
            }
        }
    }

    private fun switchToDestFragment(destTag: String) {
        if (destTag === lastFragmentTag) {
            return
        }
        if (!supportFragmentTag.contains(destTag)) {
            return
        }
        var fragment = supportFragmentManager.findFragmentByTag(destTag)
        val lastFragment = supportFragmentManager.findFragmentByTag(lastFragmentTag)
        lastFragmentTag = destTag
        val transaction = supportFragmentManager.beginTransaction()
        if (lastFragment != null) {
            transaction.hide(lastFragment)
        }
        if (fragment == null) {
            // 从未添加过
            fragment = getDestFragment(destTag)
            transaction.add(R.id.mainContent, fragment, destTag)
        } else {
            transaction.show(fragment)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getDestFragment(destTag: String): Fragment {
        return when (destTag) {
            MAP_TAG -> MapFragment()
            DECLARE_TAG -> DeclareFragment()
            SEARCH_TAG -> SearchGarbageFragment()
            INFO_TAG -> InfoFragment()
            MINE_TAG -> MineFragment()
            else -> Fragment()  // do nothing
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainTab.onSaveInstanceState(outState)
    }


    override fun onBackPressed() {
        val currentTIme = System.currentTimeMillis()
        if (lastBackPressTime == -1L || currentTIme - lastBackPressTime >= 2000) {
            // 显示提示信息
            showBackPressTip()
            // 记录时间
            lastBackPressTime = currentTIme
        } else {
            //退出应用
            finish()
        }
    }

    private fun showBackPressTip() {
        toastNormal("再按一次退出")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == InfoFragment.REQUEST_CODE) {
                val essFileList = data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION) ?: return
                val builder = StringBuilder()
                for (file in essFileList) {
                    builder.append(file).append("\n")
                }
                if (!isFinishing) {
                    loadingDialog = MaterialDialog(this)
                        .cancelable(false)
                        .cancelOnTouchOutside(false)
                        .cornerRadius(12f)
                        .customView(R.layout.layout_upload_progress_dialog_view)
                        .lifecycleOwner(this)
                    loadingDialog?.getCustomView()?.run {
                        circleProgressBar = this.findViewById(R.id.progressBar)
                        setProgress()
                    }
                    loadingDialog?.show()
                }
            }
        }
    }

    private fun setProgress() {
        if (circleProgressBar == null) {
            return
        }
        object : Thread() {
            override fun run() {
                for (i in 1..100) {
                    circleProgressBar?.setProgress(i)
                    try {
                        sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    toastSuccess("上传成功")
                    loadingDialog?.dismiss()
                }

            }
        }.start()
    }

    companion object {
        private const val MAP_TAG = "map"
        private const val DECLARE_TAG = "declare"
        private const val SEARCH_TAG = "search"
        private const val INFO_TAG = "info"
        private const val MINE_TAG = "mine"
    }
}