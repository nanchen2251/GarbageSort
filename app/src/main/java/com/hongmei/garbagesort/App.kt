package com.hongmei.garbagesort

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
    }
}