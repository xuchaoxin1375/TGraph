package com.zjgsu.cxxu.TGraph.mvp.view.workspace

import android.view.View
/**
 * define the long click abstract method */
interface RecyclerItemLongClickListener {
    fun onItemLongClick(view: View?, position: Int)
}
