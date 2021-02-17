package com.hongmei.garbagesort.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.hongmei.garbagesort.MainActivity
import com.hongmei.garbagesort.R
import com.hongmei.garbagesort.base.BaseActivity
import com.hongmei.garbagesort.ext.hideSoftKeyboard
import com.hongmei.garbagesort.register.RegisterActivity
import kotlinx.android.synthetic.main.login_activity.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.*


/**
 * 登录页面
 */
class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun layoutId(): Int {
        return R.layout.login_activity
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            loginSub.modifyAttribute(fillColor = it)
            loginRegister.setTextColor(it)
        }
        // 清空账号信息
        clearIv.setOnClickListener {
            usernameText.setText("")
        }
        // 登录
        loginSub.setOnClickListener {
            if (mViewModel.login(usernameText.text.toString(), pwdText.text.toString())) {
                startActivity(Intent(this, MainActivity::class.java))
                appViewModel.userinfo.value?.apply {
                    username = usernameText.text.toString()
                    nickname = "测试账号" + usernameText.text.toString()
                    type = loginSpinner.selectedIndex
                }
                finish()
            }
        }
        // 点击去注册
        loginRegister.setOnClickListener {
            hideSoftKeyboard(this)
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        // 密码显示
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            pwdText.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            pwdText.setSelection(pwdText.text.length)
        }
        val typeList: List<String> = LinkedList(listOf("普通用户", "监察人员", "执行人员", "管理人员", "政府人员"))
        loginSpinner.attachDataSource(typeList)
    }


}