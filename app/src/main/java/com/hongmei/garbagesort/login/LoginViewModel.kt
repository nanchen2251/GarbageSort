package com.hongmei.garbagesort.login

import com.hongmei.garbagesort.ext.toastError
import com.hongmei.garbagesort.ext.toastWarning
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * Date: 2021-02-02
 * Desc:
 */
class LoginViewModel : BaseViewModel() {

    fun login(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            toastWarning("请填写账号")
            return false
        }
        if (password.isEmpty()) {
            toastWarning("请填写密码")
            return false
        }
        if (password.length < 6) {
            toastWarning("密码不能小于6位！")
            return false
        }
        // 测试数据，账号密码均为 123456 才可以登录
        if (username != "123456" || password != "123456") {
            toastError("账号或者密码不正确！")
            return false
        }
        return true
    }
}