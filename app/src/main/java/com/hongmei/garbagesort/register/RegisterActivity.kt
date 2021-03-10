package com.hongmei.garbagesort.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.hongmei.garbagesort.MainActivity
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.bean.UserInfo
import com.hongmei.garbagesort.ext.initClose
import com.hongmei.garbagesort.ext.toastWarning
import com.hongmei.garbagesort.login.LoginViewModel
import com.hongmei.garbagesort.util.CacheUtil
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.register_activity.*
import java.util.*


/**
 * 注册页面
 */
class RegisterActivity : BaseActivity<LoginViewModel>() {

    override fun layoutId(): Int = R.layout.register_activity

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            registerSub.modifyAttribute(fillColor = it)
            toolbar.setBackgroundColor(it)
        }
        toolbar.initClose("注册") {
            finish()
        }
        val typeList: List<String> = LinkedList(listOf("普通用户", "监察人员", "执行人员", "政府管理人员"))
        registerSpinner.attachDataSource(typeList)
        // 清空账号信息
        registerClear.setOnClickListener {
            registerUsername.setText("")
        }
        // 注册
        registerSub.setOnClickListener {
            if (registerUsername.text.isEmpty()) {
                toastWarning("请填写账号")
                return@setOnClickListener
            }
            if (registerPwd.text.isEmpty()) {
                toastWarning("请填写密码")
                return@setOnClickListener
            }
            if (registerPwd1.text.isEmpty()) {
                toastWarning("请填写确认密码")
                return@setOnClickListener
            }
            if (registerPwd1.text.length < 6) {
                toastWarning("密码不能小于6位！")
                return@setOnClickListener
            }
            if (registerPwd.text.toString() != registerPwd1.text.toString()) {
                toastWarning("两次输入的密码不一致")
                return@setOnClickListener
            }
            appViewModel.userinfo.value = UserInfo(
                username = registerUsername.text.toString(),
                nickname = "测试账号" + registerUsername.text.toString(),
                type = registerSpinner.selectedIndex
            )
            CacheUtil.setUser(appViewModel.userinfo.value)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        // 密码显示
        registerKey.setOnCheckedChangeListener { _, isChecked ->
            registerPwd.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            registerPwd.setSelection(registerPwd.text.length)
        }
        registerKey1.setOnCheckedChangeListener { _, isChecked ->
            registerPwd1.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            registerPwd1.setSelection(registerPwd1.text.length)
        }
    }
}