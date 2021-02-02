package com.hongmei.garbagesort.weight.loadCallBack


import com.hongmei.garbagesort.R
import com.kingja.loadsir.callback.Callback


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}