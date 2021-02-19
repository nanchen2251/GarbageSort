package com.hongmei.garbagesort.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseFragment
import com.hongmei.garbagesort.base.CommonViewPagerAdapter
import com.hongmei.garbagesort.ext.init
import com.hongmei.garbagesort.ext.showMessage
import com.hongmei.garbagesort.ext.toastError
import com.wyt.searchbox.SearchFragment
import com.wyt.searchbox.custom.IOnSearchClickListener
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.search_garbage_fragment.*


/**
 * Date: 2021-02-02
 * Desc: 搜索页面
 */
class SearchGarbageFragment : BaseFragment<SearchGarbageViewModel>(), Toolbar.OnMenuItemClickListener, IOnSearchClickListener {
    private val searchFragment by lazy { SearchFragment.newInstance() }
    private val titles by lazy { arrayOf("可回收物", "有害垃圾", "湿垃圾", "干垃圾") }

    override fun layoutId(): Int {
        return R.layout.search_garbage_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {
        toolbar.init("垃圾分类搜索")
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener(this)
        searchFragment.setOnSearchClickListener(this)
        setHasOptionsMenu(true)

        // 加载下方的垃圾分类知识
        activity?.run {
            val infoPagerAdapter = CommonViewPagerAdapter(supportFragmentManager, titles)
            infoPagerAdapter.addFragment(RecyclableFragment())
            infoPagerAdapter.addFragment(HarmfulFragment())
            infoPagerAdapter.addFragment(WetFragment())
            infoPagerAdapter.addFragment(DryFragment())
            searchViewPager.adapter = infoPagerAdapter
            searchTab.setupWithViewPager(searchViewPager)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // 加载菜单文件
        inflater.inflate(R.menu.menu_main, menu)
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> searchFragment.showFragment(activity?.supportFragmentManager, SearchFragment.TAG)
        }
        return true
    }

    override fun OnSearchClick(keyword: String?) {
        if (keyword.isNullOrEmpty()) {
            return
        }
        showLoading()
        AndroidNetworking.get("http://api.tianapi.com/txapi/lajifenlei/index?key=1330d754efab189f0553c3c4f43e88d8&word=$keyword")
            .build()
            .getAsObject(GarbageData::class.java, object : ParsedRequestListener<GarbageData> {
                override fun onResponse(response: GarbageData?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("查询失败，请稍后再试")
                        return
                    }
                    if (response.code != 200) {
                        showMessage("${keyword}不是垃圾")
                        return
                    }
                    // 可能搜索出多个结果，分别处理
                    val sbf = StringBuffer()
                    response.newslist.forEach {
                        sbf.append(it.name)
                        sbf.append("是")
                        sbf.append(getGarbageType(it.type))
                        sbf.append("\n")
                    }
                    if (sbf.isNotEmpty()) {
                        showMessage(sbf.substring(0, sbf.length - 1))
                    }
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError("查询失败，请稍后再试")
                }

            })
    }

    private fun getGarbageType(type: Int): String {
        return when (type) {
            0 -> "可回收物"
            1 -> "有害垃圾"
            2 -> "易腐垃圾"
            3 -> "其他(干)垃圾"
            else -> "未知类型的垃圾"
        }
    }

}