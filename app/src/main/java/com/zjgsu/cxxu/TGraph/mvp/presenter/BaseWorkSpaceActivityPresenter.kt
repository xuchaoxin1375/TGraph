package com.zjgsu.cxxu.TGraph.mvp.presenter

import com.zjgsu.cxxu.TGraph.base.BasePresenter
import com.zjgsu.cxxu.TGraph.mvp.model.FileLog
import java.util.*

interface BaseWorkSpaceActivityPresenter {
    /**
     * 内部接口:Presenter*/
    interface Presenter : BasePresenter {
        /**
         * 文件列表为空时的view
         */
        fun onEmptyView()
        /**
         * 加载导图文件(memo)
         */
        fun onLoadMemoData()

        /**
         * 删除Item
         * @param position
         */
        fun removeItemFile(position: Int)
        fun getItemFilePath(position: Int): String?
    }
    /*内部接口:ItemView*/
    interface ItemView {
        /**
         * 显示空的View
         */
        fun showEmptyView()

        /**
         * 设置ListView的数据
         *
         * @param listData
         */
        fun setListData(listData: ArrayList<FileLog>)

        /**
         * 刷新ListView的数据
         */
        fun refreshListData()

        open fun getTestDefaultPath(): String
    }

}