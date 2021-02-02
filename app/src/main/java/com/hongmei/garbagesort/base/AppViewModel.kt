package com.hongmei.garbagesort.base

import com.hongmei.garbagesort.bean.UserInfo
import com.hongmei.garbagesort.util.CacheUtil
import com.hongmei.garbagesort.util.SettingUtil
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.UnPeekLiveData

/**
 * 描述　:APP全局的ViewModel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppViewModel : BaseViewModel() {

    //App的账户信息
    var userinfo = UnPeekLiveData<UserInfo>()

    //App主题颜色 中大型项目不推荐以这种方式改变主题颜色，比较繁琐耦合，且容易有遗漏某些控件没有设置主题色
    var appColor = UnPeekLiveData<Int>()

    //App 列表动画
    var appAnimation = UnPeekLiveData<Int>()

    init {
        //默认值保存的账户信息，没有登陆过则为null
        userinfo.value = CacheUtil.getUser()
        //默认值颜色
        appColor.value = SettingUtil.getColor(appContext)
        //初始化列表动画
        appAnimation.value = SettingUtil.getListMode()
    }
}