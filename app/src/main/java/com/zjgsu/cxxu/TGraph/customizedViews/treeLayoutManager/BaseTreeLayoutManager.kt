package com.zjgsu.cxxu.TGraph.customizedViews.treeLayoutManager

import com.zjgsu.cxxu.TGraph.mvp.model.TreeViewSize
import com.zjgsu.cxxu.TGraph.customizedViews.NodeView
import com.zjgsu.cxxu.TGraph.customizedViews.TreeView

/**
 * 计算位置
 * 确认大小
 * 修正位置
 */
interface BaseTreeLayoutManager {
    /**
     * 树形结构的位置计算
     */
    fun onTreeLayout(treeView: TreeView?)

    /**确认ViewGroup的大小
     * 位置分布好后的回调,用于确认ViewGroup的大小
     * 返回TreeViewSize类型值
     */
    fun onTreeLayoutCallBack(): TreeViewSize?

    /**
     * 修正位置
     *
     * @param treeView
     * @param next
     */
    fun correctTreeLayout(treeView: TreeView, next: NodeView?)
}