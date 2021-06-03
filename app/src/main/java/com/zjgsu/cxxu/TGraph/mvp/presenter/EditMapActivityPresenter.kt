package com.zjgsu.cxxu.TGraph.mvp.presenter

import Tree
import android.text.TextUtils
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.mvp.view.editmap.editInterface.EditMapManager
import com.zjgsu.cxxu.TGraph.util.AppUtil.androidSystemVersion
import com.zjgsu.cxxu.TGraph.util.StringTool.isEmpty
import com.zjgsu.cxxu.TGraph.util.file.FileInformation
import com.zjgsu.cxxu.TGraph.util.file.MemoFileCreate
import java.io.File
import java.io.IOException
import java.io.InvalidClassException
import java.text.SimpleDateFormat
import java.util.*

/**
 * EditMapActivityPresenter will initially implements(override) the methods;
 * however the EditMapManager.Presenter is not the root interface definition,it is a inner interface of the EditMapManager,and the member(nested) interface of the EditMapManager,the inner interface could implement its
 * exclusive interface (there, it implements from the basePresenter.
 */
class EditMapActivityPresenter(private val mView: EditMapManager.View) : EditMapManager.Presenter {
    companion object {
        private const val TAG = "EditMapActivityPresenter"
    }

    private var mIsCreate = false
    private var mAbsFilePath: String? = null
    private var mDefaultFilePath: String? = null
    private var mFileName: String? = null

    private var mTree: Tree<String?>? = null
    private var mOldTree: Tree<String>? = null
    private var mMemoFilesArray: Array<String>? = null
    override fun start() {
        mIsCreate = true
        /*默认路径字符串采用资源和视图分离*/
        mFileName = mView.defaultPlanStr
        //        mView.showLoadingFile();
    }

    override fun onRecycle() {
        mMemoFilesArray = null
        mTree = null
    }

    override fun setSavePath(path: String?) {
        // 获取到是否是编辑文件
        // 文件的名字
        // 文件路径下的memo file lists
        mIsCreate = false
        d(TAG, "memo file path=${path!!}")
        mAbsFilePath = path
        val fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
        d(TAG,"fileName=$fileName(without directory prefix and extension..)")
        mFileName = fileName
        refreshMemoFilesLists()
    }

    override fun createDefaultTree() {
        val plan = Node(mView.defaultPlanStr)
        mTree = Tree(plan)
        if (mTree != null) {

            mView.setTreeViewData(mTree)
        }

        refreshMemoFilesLists()
    }

    override fun refreshMemoFilesLists() {
        if (!TextUtils.isEmpty(mAbsFilePath)) {
            val editFilePath = File(mAbsFilePath)
            val lists = editFilePath.parentFile.list()
            //设置集合
            sortFiles(lists)
        } else {
            // 默认文件路径
            mDefaultFilePath = mView.defaultSaveFilePath
            val file = File(mDefaultFilePath)
            if (!file.exists()) {
                file.mkdirs()
                d(TAG,"creating default file path")
            }
            d(TAG,"defaultFilePath:"+mDefaultFilePath!!)
            if (file.exists()) {
                val lists = file.list()
                sortFiles(lists)
            } else {
                d(TAG, "defaultPath is empty!")
            }
        }
    }

    private fun sortFiles(pLists: Array<String>?) {
        val memoFiles = ArrayList<String>()
        var str: String
        if (pLists != null) {
            for (fileName in pLists) {
                if (fileName.endsWith(".test")) {
                    d(TAG,"file=$fileName")
                    str = fileName.substring(0, fileName.lastIndexOf("."))
                    if (!isEmpty(str)) {
                        memoFiles.add(fileName)
                    }

                    //编辑模式，不能修改文件名字
                    if (!mIsCreate) {
                        memoFiles.remove(fileName)
                    }
                }
            }
        }
        if (memoFiles.size > 0) {
            mMemoFilesArray = memoFiles.toTypedArray()
            for (str in mMemoFilesArray!!) {
                d(TAG,"mTestFilesArray str=$str")
            }
        }
    }

    /*解析导图文件,并将结果记录在成员变量mFilepath中*/
    override fun parseMemoFile() {
        //读取导图文件
        if (!isEmpty(mAbsFilePath)) {
            try {
                val memoFileCreate = MemoFileCreate()
                d(TAG,"filePath=${mAbsFilePath!!}")
                val o = memoFileCreate.readContentObject(mAbsFilePath!!)
                val tree = o as Tree<String?>?
                mTree = tree
                mView.setTreeViewData(mTree)
                mIsCreate = false
                //拷贝一份
                mOldTree = mTree!!.deepClone() as Tree<String>
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvalidClassException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun addBrotherNode() {
        mView.showAddNoteDialog()
    }

    override fun addSubNote() {
        mView.showAddSubNoteDialog()
    }

    override fun editNote() {
        mView.showEditNoteDialog()
    }

    override fun focusCenter() {
        mView.focusingMid()
    }

    override fun saveFile() {
        //TODO 进行判断是否改变了文本
        //只有在编剧模式下才进行判断其他的跳过
        var equals = false
        if (!mIsCreate) {
            //进行判断
            equals = isEqualsOldTree
        }
        if (equals) {
            d("no change :", "true")
            mView.finishActivity()
        } else {
            d("change :", "false")
            mView.showSaveFileDialog(mAbsFilePath)
        }
    }


    private val isEqualsOldTree: Boolean
        private get() {
            var equals = false
            val temp = mTree
            val compareTemp = mOldTree
            val tempBuffer = StringBuffer()
            val stack = Stack<Node<String?>?>()
            val rootNode = temp!!.getRootNode()
            stack.add(rootNode)
            while (!stack.isEmpty()) {
                val pop = stack.pop()
                tempBuffer.append(pop!!.value)
                val childNodes = pop.childNodes
                for (item in childNodes) {
                    stack.add(item)
                }
            }
            val compareTempBuffer = StringBuffer()
            val stackThis = Stack<Node<String>?>()
            val rootNodeThis = compareTemp!!.getRootNode()
            stackThis.add(rootNodeThis)
            while (!stackThis.isEmpty()) {
                val pop = stackThis.pop()
                compareTempBuffer.append(pop!!.value)
                val childNodes = pop.childNodes
                for (item in childNodes) {
                    stackThis.add(item)
                }
            }
            if (compareTempBuffer.toString() == tempBuffer.toString()) {
                equals = true
            }
            return equals
        }

    override fun doSaveFile(fileName: String?) {
        val testFileCreate = MemoFileCreate()
        testFileCreate.createTestMapsDirectory()
        testFileCreate.createTempDirectory()
        val fileInfo = FileInformation()
        val time = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        fileInfo.date = simpleDateFormat.format(time)
        //        fileInfo.app_version = mView.getAppVersion();
        fileInfo.android_version = androidSystemVersion
        fileInfo.map_name = mTree!!.getRootNode()!!.value
        testFileCreate.writeLog(fileInfo)
        testFileCreate.writeContent(mTree!!)
        testFileCreate.makeTestFile(fileName!!)
        testFileCreate.deleteTemp()
    }

    override var tree: Tree<String?>?
        get() = mTree
        set(Tree) {
            mTree = Tree
            mView.setTreeViewData(mTree)
        }
    override val memoList: List<*>
        get() {
            val list = ArrayList<Any?>()
//            val list=ArrayList<String>()
            d("isCreate:", mIsCreate.toString())
            if (mMemoFilesArray != null) {
                for (s in mMemoFilesArray!!) {
                    list.add(s)
                    d("exist file:", s)
                }
            } else {
                d(TAG, "mTestFilesArray is empty")
            }
            return list
        }
    override val saveInput: String?
        get() = if (mIsCreate) {
            mTree!!.getRootNode()!!.value
        } else {
            mFileName
        }


}