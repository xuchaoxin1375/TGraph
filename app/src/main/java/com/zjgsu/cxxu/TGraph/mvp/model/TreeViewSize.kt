package com.zjgsu.cxxu.TGraph.mvp.model

import com.zjgsu.cxxu.TGraph.Lg

/**
 *
 * 用于记录/提供操作当前树形图所在容器控件的大小等信息
 */
class TreeViewSize {
    companion object {
        private const val TAG = "TreeViewSize"
    }

    @JvmField
    var top = 0

    @JvmField
    var left = 0

    @JvmField
    var right = 0

    @JvmField
    var bottom = 0
    fun clear() {
        Lg.d(TAG, "reset the TreeViewSize instance")
        top = 0
        left = 0
        right = 0
        bottom = 0
    }

    override fun toString(): String {
        return "TreeViewSize{" +
                "top=" + top +
                ", left=" + left +
                ", right=" + right +
                ", bottom=" + bottom +
                '}'
    }
}