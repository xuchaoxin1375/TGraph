package com.zjgsu.cxxu.TGraph.mvp.view.workspace

import android.view.View

/**
 * 为文件列表设置点击时间的接口*/
interface RecyclerItemClickListener {
    fun onItemClick(view: View, position: Int)
}