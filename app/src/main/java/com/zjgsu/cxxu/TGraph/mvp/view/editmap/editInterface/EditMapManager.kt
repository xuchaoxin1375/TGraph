package com.zjgsu.cxxu.TGraph.mvp.view.editmap.editInterface

import Tree
import com.zjgsu.cxxu.TGraph.base.BasePresenter
import com.zjgsu.cxxu.TGraph.mvp.model.Node

interface EditMapManager {
    /**
     * 声明一个基于EditMapContract类内部所定义的一个内部类引用变量
     * 嵌套接口nested interface:
     */
    interface Presenter : BasePresenter {
        /**
         * 设置树形模型
         */
        var tree: Tree<String?>?

        /**
         * 获取文件目录下的导图文件集合
         */
        /*the List type in java  <=> List<*>? in kotlin.*/
        val memoList: List<*>?
        val saveInput: String?
        /**
         * 设置读取的文件路径
         */
        fun setSavePath(path: String?)

        /**
         * 刷新文件路径下的导图文件集合
         */
        fun refreshMemoFilesLists()

        /**
         * 读取导图文件
         */
        fun parseMemoFile()

        /**
         * 创建默认的Tree
         */
        fun createDefaultTree()

        /**
         * 添加节点
         */
        fun addBrotherNode()

        /**
         * 添加子节点
         */
        fun addSubNote()

        /**
         * 编辑节点
         */
        fun editNote()

        /**
         * 对焦中心
         */
        fun focusCenter()

        /**
         * 保存文件
         */
        fun saveFile()

        /**
         * 进行保存
         */
        fun doSaveFile(fileName: String?)
        /**
         * 获取树形模型
         *
         * @return 树形模型
         */
    }

    /**
     * these methods implemented in the EditMapActivity class
     * (press ctrl+alt+B)you could go to there(the icon in the left line mark is also available)
     *
     * nested interface:ItemView
     * the nested ItemView interface is very different with the
     */
    interface View {


        /**
         * 设置树形结构数据
         * @param Tree
         */
        fun setTreeViewData(Tree: Tree<String?>?)

        /**
         * 显示添加节点
         */
        fun showAddNoteDialog()

        /**
         * 显示添加子节点
         */
        fun showAddSubNoteDialog()

        /**
         * 显示编辑节点
         */
        fun showEditNoteDialog()

        /**
         * 显示保存数据
         * @param fileName
         */
        fun showSaveFileDialog(fileName: String?)

        /**
         * 对焦中心
         */
        fun focusingMid()

        /**
         * 获得默认root节点的text
         *
         * @return
         */
        val defaultPlanStr: String?

        /**
         * 获得最近对焦
         *
         * @return
         */
        val currentFocusNode: Node<String>?

        /**
         * 获取Plan的默认字符
         *
         * @return Title
         */
        val defaultSaveFilePath: String?

        /**
         * 获得app的版本
         *
         * @return 版本号
         */
//        val appVersion: String?
        fun finishActivity()
    }
}