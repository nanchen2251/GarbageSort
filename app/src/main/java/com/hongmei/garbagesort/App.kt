package com.hongmei.garbagesort

import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.MapsInitializer
import com.amap.api.services.core.ServiceSettings
import com.tencent.mmkv.MMKV
import me.hgj.jetpackmvvm.base.BaseApp

/**
 * Date: 2021-02-02
 * Desc:
 */
class App : BaseApp() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        val appKey = "a4ad336903a99170fbea7fa7bdb9e3d5"
        MapsInitializer.setApiKey(appKey)
        ServiceSettings.getInstance().setApiKey(appKey)
        AMapLocationClient.setApiKey(appKey)
    }
}