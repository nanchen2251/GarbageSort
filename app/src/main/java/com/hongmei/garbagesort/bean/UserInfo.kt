package com.hongmei.garbagesort.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　: 账户信息
 */
@Parcelize
data class UserInfo(
    var admin: Boolean = false,
    var chapterTops: List<String> = listOf(),
    var collectIds: MutableList<String> = mutableListOf(),
    var email: String = "",
    var icon: String = "",
    var id: String = "",
    var nickname: String = "",
    var password: String = "",
    var token: String = "",
    var type: Int = 0,
    var username: String = ""
) : Parcelable

object UserType{
    const val GENERAL = 0 // 普通用户
    const val SUPERVISOR = 1 // 监察人员
    const val EXECUTOR = 2 // 执行人员
    const val MANAGER = 3 // 管理人员
    const val GOVERNMENT = 3 // 政府人员
}