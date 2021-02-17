package com.hongmei.garbagesort.ext

import es.dmoral.toasty.Toasty


/**
 * Toast相关的拓展
 */


fun toastNormal(message: String) {
    Toasty.normal(appContext, message).show()
}


fun toastSuccess(message: String) {
    Toasty.success(appContext, message).show()
}

fun toastError(message: String) {
    Toasty.error(appContext, message).show()
}

fun toastWarning(message: String) {
    Toasty.warning(appContext, message).show()
}