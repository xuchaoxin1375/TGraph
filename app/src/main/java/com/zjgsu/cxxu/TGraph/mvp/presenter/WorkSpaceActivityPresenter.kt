package com.zjgsu.cxxu.TGraph.mvp.presenter

import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.mvp.model.FileLog
import com.zjgsu.cxxu.TGraph.util.AppUtil
import java.io.File


class WorkSpaceActivityPresenter(private val mView: BaseWorkSpaceActivityPresenter.ItemView) : BaseWorkSpaceActivityPresenter.Presenter {
    companion object {
        private const val TAG = "WorkSpaceActivityPresenter"

    }

    private lateinit var mLists: ArrayList<FileLog>
    private var mDefaultPath: String? = null

    override fun start() {
        mLists = ArrayList()
    }

    override fun onRecycle() {}
    override fun onEmptyView() {
        mView.showEmptyView()
    }

    override fun onLoadMemoData() {
        if (mDefaultPath == null) {
            /*设置默认的memo存储路径*/
            mDefaultPath = mView.getTestDefaultPath()
        }
        val saveFilePath = File(mDefaultPath)
        Lg.d(TAG, "test the execution of the onLoadTestData():the $saveFilePath has gotten")
        mLists!!.clear()
        if (saveFilePath.exists()) {
            val files = saveFilePath.listFiles()
            if (files != null) {
                Lg.d(TAG, "the files reference to null$files")
                for (file in files) {
                    if (file.isFile && file.absolutePath.endsWith(".memo")) {
                        val model = FileLog()
                        model.filePath = file.absolutePath
                        model.editTime = AppUtil.transferLongToDate("yyyy-MM-dd HH:mm:ss", file.lastModified())
                        var fileName = file.name
                        if (fileName.indexOf(".") > 0) {
                            fileName = fileName.substring(0, fileName.indexOf("."))
                        }
                        model.mapRoot = fileName
                        mLists!!.add(model)
                    }
                }
            }

        }
        mView.setListData(mLists)
    }

    /**
     * the implement of delete the item of the files*/
    override fun removeItemFile(position: Int) {
        val currentFile: FileLog = mLists!![position]
        val file = File(currentFile.filePath)
        if (file.exists()) {
            file.delete()
            /**
             * java.util.ArrayList<E> public boolean remove(Object o)
            Removes the first occurrence of the specified element from this list, if it is present.
            If the list does not contain the element, it is unchanged. */
            mLists.remove(currentFile)
        }
    }

    override fun getItemFilePath(position: Int): String? {
        return mLists?.get(position)?.filePath
    }
}

