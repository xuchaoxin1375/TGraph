package com.zjgsu.cxxu.TGraph.mvp.view.editmap

import Tree
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.base.BaseActivity
import com.zjgsu.cxxu.TGraph.constants.AppConstants
import com.zjgsu.cxxu.TGraph.customizedViews.TreeView
import com.zjgsu.cxxu.TGraph.customizedViews.treeLayoutManager.TreeLayoutManager
import com.zjgsu.cxxu.TGraph.customizedViews.viewInterfaces.TreeViewItemLongClick
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.mvp.presenter.EditMapActivityPresenter
import com.zjgsu.cxxu.TGraph.mvp.view.EditAlertDialog
import com.zjgsu.cxxu.TGraph.mvp.view.EditAlertDialog.DeleteCallBack
import com.zjgsu.cxxu.TGraph.mvp.view.EditAlertDialog.EnterCallBack
import com.zjgsu.cxxu.TGraph.mvp.view.editmap.editInterface.EditMapManager
import com.zjgsu.cxxu.TGraph.util.DensityUtils.dp2px
import com.zjgsu.cxxu.TGraph.util.file.JudgeNew

class EditMapActivity : BaseActivity(), EditMapManager.View {
    //    notification test:
    companion object {
        private const val TAG = "EditMapActivity"
        private const val tree_model = "tree_model"
    }

    lateinit var manager: NotificationManager
    private lateinit var saveDefaultFilePath: String

    /**o'n
     * 声明一个基于EditMapContract类内部所定义的一个内部类引用变量
     * 嵌套接口
     */
    private var mEditMapPresenter: EditMapManager.Presenter? = null
    private var mEditMapTreeView: TreeView? = null
    private var mBtnAddSub: Button? = null
    private var mBtnAddNode: Button? = null
    private var mBtnFocusMid: Button? = null

    /*这些控件实例的引用在kotlin中可以使用lateinit来处理*/
    private var addSubNodeDialog: EditAlertDialog? = null
    private var addNodeDialog: EditAlertDialog? = null
    private var editNodeDialog: EditAlertDialog? = null
    private var saveFileDialog: EditAlertDialog? = null

    //    lateinit var messageText: EditText
    override fun onPause() {
        super.onPause()
//        val content = messageText.text.toString()
        val content = "you saved the map."
//        set and show the notification
        d(TAG, "preparing the notification...")
        createNotificationChannel()
        val notification = buildNotificationContent()
        manager.notify(1, notification)
    }

    override fun getResId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_edit_map
    }

    /**
     * 执行(调用)视图绑定方法,同时设置监听事件:
     */
    override fun onBaseBindView() {
        bindViews()
        /**
         * android.view.ItemView public void setOnClickListener(ItemView.OnClickListener l)
         * @param: l – The callback that will run
         * Register a callback to be invoked when this view is clicked. If this view is not clickable, it becomes clickable.
         * btnFocusMid.setOnClickListener(new ItemView.OnClickListener() {
         * @Override
         * public void onClick(ItemView v) {
         * mEditMapPresenter.focusMid();
         * }
         * });
         */
        mBtnAddNode!!.setOnClickListener { v: View? -> mEditMapPresenter!!.addBrotherNode() }
        mBtnAddSub!!.setOnClickListener { v: View? -> mEditMapPresenter!!.addSubNote() }
        mBtnFocusMid!!.setOnClickListener { v: View? -> mEditMapPresenter!!.focusCenter() }
        /**
         * android.content.ContextWrapper
         * public android.content.Context getApplicationContext()
         * Return the context of the single, global Application object of the current process. This generally should only be used if you need a Context whose lifecycle is separate from the current context, that is tied to the lifetime of the process rather than the current component.
         */
        val dx = dp2px(applicationContext, 20f)
        val dy = dp2px(applicationContext, 20f)
        val screenHeight = dp2px(applicationContext, 720f)
        mEditMapTreeView!!.setTreeLayoutManager(TreeLayoutManager(dx, dy, screenHeight))
        /*分别设置应点击事件:*/mEditMapTreeView!!.setTreeViewItemClick()
        mEditMapTreeView!!.setTreeViewItemLongClick(object : TreeViewItemLongClick {
            override fun onLongClick(view: View?) {
                mEditMapPresenter!!.editNote()
            }
        })
        initPresenter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(tree_model, mEditMapPresenter!!.tree)
        Log.i(TAG, "onSaveInstanceState: keep the original data!you didn't do any modify")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saveZable = savedInstanceState.getSerializable(tree_model)
        mEditMapPresenter!!.tree = saveZable as Tree<String?>?
    }

    override fun onLoadData() {}

    override fun setTreeViewData(Tree: Tree<String?>?) {
        mEditMapTreeView?.tree = Tree as Tree<String>
    }

    override fun showAddNoteDialog() {
        if (mEditMapTreeView!!.mCurrentFocusNode!!.parentNode == null) {
//            String str=getString(R.string.cannot_add_brother_node_with_root);
//            AppUtil.showToast(str, Toast.LENGTH_LONG);//with the extension funciton with kotlin feature
//            AppUtil.showToast("test");//with kotlin's feature(the default parameter)
            Toast.makeText(this, getString(R.string.cannot_add_brother_node_with_root), Toast.LENGTH_SHORT).show()
        } else if (addNodeDialog == null) {
            val factory = LayoutInflater.from(this)
            val inflate = factory.inflate(R.layout.dialog_edit_input, null)
            addNodeDialog = EditAlertDialog(this@EditMapActivity)
            addNodeDialog!!.setView(inflate)
            addNodeDialog!!.setDivTitle(getString(R.string.add_a_same_floor_node))
            addNodeDialog!!.addEnterCallBack(object : EnterCallBack {
                override fun onEdit(value: String?) {
                    var value = value
                    if (TextUtils.isEmpty(value)) {
                        value = getString(R.string.null_node)
                    }
                    mEditMapTreeView!!.addBrotherNode(value!!)
                    clearDialog(addNodeDialog)
                    if (addNodeDialog != null && addNodeDialog!!.isShowing) addNodeDialog!!.dismiss()
                }
            })
            addNodeDialog!!.show()
        } else {
            addNodeDialog!!.clearInput()
            addNodeDialog!!.show()
        }
    }

    override fun showAddSubNoteDialog() {
        if (addSubNodeDialog == null) {
            val factory = LayoutInflater.from(this)
            val inflate = factory.inflate(R.layout.dialog_edit_input, null)
            addSubNodeDialog = EditAlertDialog(this)
            addSubNodeDialog!!.setView(inflate)
            addSubNodeDialog!!.setDivTitle(getString(R.string.add_a_sub_node))
            addSubNodeDialog!!.addEnterCallBack(object : EnterCallBack {
                override fun onEdit(value: String?) {
                    var value = value
                    if (TextUtils.isEmpty(value)) {
                        value = getString(R.string.null_node)
                    }
                    mEditMapTreeView!!.addSubNode(value!!)
                    clearDialog(addSubNodeDialog)
                }
            })
            addSubNodeDialog!!.show()
        } else {
            addSubNodeDialog!!.clearInput()
            addSubNodeDialog!!.show()
        }
    }

    override fun showEditNoteDialog() {
        if (editNodeDialog == null) {
            val factory = LayoutInflater.from(this)
            val view = factory.inflate(R.layout.dialog_edit_input, null)
            editNodeDialog = EditAlertDialog(this)
            editNodeDialog!!.setView(view)
            editNodeDialog!!.setDivTitle(getString(R.string.edit_node))
        }
        editNodeDialog!!.setNodeModel(currentFocusNode!!)
        editNodeDialog!!.setInput(currentFocusNode!!.value)
        editNodeDialog!!.addDeleteCallBack(object : DeleteCallBack {
            override fun onDeleteModel(model: Node<String>?) {
                try {
                    mEditMapTreeView!!.deleteNode(model!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onDelete() {}
        })
        editNodeDialog!!.addEnterCallBack(object : EnterCallBack {
            override fun onEdit(value: String?) {
                var value = value
                if (TextUtils.isEmpty(value)) {
                    value = getString(R.string.null_node)
                }
                mEditMapTreeView!!.modifyNodeValue(currentFocusNode, value!!)
                clearDialog(editNodeDialog)
            }
        })
        editNodeDialog!!.show()
    }

    override fun showSaveFileDialog(fileName: String?) {
        /*应当再此处检查重名:*/
        JudgeNew.is_save_dialog=true
        //同名文件提示更改
//        val inputStr = EditAlertDialog.input
//        d(TAG, "checking the input text...:$inputStr")
//        val fileNameList= AppUtil.getFileList("/storage/emulated/0/TestMaps/")
//        val bool1= JudgeNew.opened_name==inputStr+".memo"
//        d(TAG,"JudgeNew.opened_name==inputStr+\".memo=${bool1}")
//        if(bool1){
//            JudgeNew.isNew=false
//        }

        if (saveFileDialog == null) {
            val factory = LayoutInflater.from(this)
            val view = factory.inflate(R.layout.dialog_edit_input, null)
            saveFileDialog = EditAlertDialog(this)
            saveFileDialog!!.setView(view)
            saveFileDialog!!.setDivTitle(getString(R.string.save_file))
        }
        //如果是编辑文本时可能已经有文件名了，需要进行读取文件的名字
        saveFileDialog!!.setInput(mEditMapPresenter!!.saveInput)
        saveFileDialog!!.setConditionDeleteTextValue(getString(R.string.exit_edit))

        //获取文件目录下的已经存在的文件集合(kotlin协变)
        saveFileDialog!!.setFileLists(mEditMapPresenter!!.memoList as List<String>?)
        saveFileDialog!!.addEnterCallBack(object : EnterCallBack {
            override fun onEdit(value: String?) {
                mEditMapPresenter!!.doSaveFile(value)

                //退出文件
                clearDialog(saveFileDialog)
                finish()
            }
        })
        saveFileDialog!!.addDeleteCallBack(object : DeleteCallBack {
            override fun onDeleteModel(nodeModel: Node<String>?) {}
            override fun onDelete() {
                finish()
            }
        })
        saveFileDialog!!.show()
    }

    override fun focusingMid() {
        mEditMapTreeView!!.focusMidLocation()
    }

    override val defaultPlanStr: String
        get() = getString(R.string.defualt_my_plan)
    override val currentFocusNode: Node<String>?
        get() = mEditMapTreeView!!.mCurrentFocusNode
    override val defaultSaveFilePath: String
        get() {
            saveDefaultFilePath = Environment.getExternalStorageDirectory().path + AppConstants.test_maps
            d("saveDefaultFilePath:", saveDefaultFilePath!!)
            return saveDefaultFilePath
        }

    override fun finishActivity() {
        finish()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TODO 判断一下文本是否改变了
            mEditMapPresenter!!.saveFile()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        mEditMapPresenter!!.onRecycle()
        super.onDestroy()
    }


    private fun createNotificationChannel() {
        d(TAG, "creating NotificationChannel...")
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            val channel2 =
                NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel2)
        }
    }

    private fun buildNotificationContent(): Notification {
        //            Set the notification's tap action
//        val intent = Intent(this, NotificationActivity::class.java)
//
//        val pi = PendingIntent.getActivity(this, 0, intent, 0)
//            Set the notification content
        d(TAG, "building the NotificationContent...")
        val notification = NotificationCompat.Builder(this, "important")
            .setContentTitle("File save")
            .setContentText("be sure to save the modify!")
            //  .setStyle(NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//           .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources, R.drawable.big_image)))
            .setSmallIcon(R.drawable.small_icon)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon))
//            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        return notification
    }

    private fun bindViews() {
        mEditMapTreeView = findViewById(R.id.edit_map_tree_view)
        mBtnAddSub = findViewById(R.id.btn_add_sub)
        mBtnAddNode = findViewById(R.id.btn_add_node)
        mBtnFocusMid = findViewById(R.id.btn_focus_mid)
    }

    private fun initPresenter() {
        //use the Presenter to operate the model(if you need) (update the view in the same time)
        mEditMapPresenter = EditMapActivityPresenter(this)
        /*Smart cast to 'EditMapActivityPresenter' is impossible, because 'mEditMapPresenter' is a mutable property that could have been changed by this time
        * in this case,you could use ?.to invoke the method*/
        mEditMapPresenter?.start()
        /**
         * public android.content.Intent getIntent()
        Return the intent that started this activity.
         */
        val intent = intent
        val data = intent.data
        if (data != null) {
            val path = data.path
            //加载导图的文件路径
            d(TAG, "setting the memo files path=$path...")//storage/emulated/0/TestMaps/....memo
            presenterSetLoadMapPath(path)
            //解析导图文件
            d(TAG, "parsing the memo file...")
            mEditMapPresenter?.parseMemoFile()
        } else {
            d(TAG, "creating the default tree...")
            mEditMapPresenter?.createDefaultTree()
        }
    }

    private fun presenterSetLoadMapPath(path: String?) {
        d(TAG, "setting the save file Path..\n you get the path=$path")
//        d(TAG,)
        mEditMapPresenter!!.setSavePath(path)
    }

    private fun clearDialog(dialog: Dialog?) {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
    }

}