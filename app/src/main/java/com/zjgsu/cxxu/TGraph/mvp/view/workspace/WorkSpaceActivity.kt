package com.zjgsu.cxxu.TGraph.mvp.view.workspace

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.base.BaseActivity
import com.zjgsu.cxxu.TGraph.constants.AppConstants
import com.zjgsu.cxxu.TGraph.constants.AppPermissions
import com.zjgsu.cxxu.TGraph.fileAdapter.FilesAdapter
import com.zjgsu.cxxu.TGraph.mvp.model.FileLog
import com.zjgsu.cxxu.TGraph.mvp.presenter.BaseWorkSpaceActivityPresenter
import com.zjgsu.cxxu.TGraph.mvp.presenter.WorkSpaceActivityPresenter
import com.zjgsu.cxxu.TGraph.mvp.view.editmap.EditMapActivity
import com.zjgsu.cxxu.TGraph.mvp.model.line.CubicInterpolator
import com.zjgsu.cxxu.TGraph.util.AppUtil
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.util.file.JudgeNew
import java.io.File

class WorkSpaceActivity : BaseActivity(), BaseWorkSpaceActivityPresenter.ItemView {
    /*members definition section:*/
    private val TAG = "WorkSpaceActivity"
    private var mPresenter: BaseWorkSpaceActivityPresenter.Presenter? = null
    private var toolBar: Toolbar? = null
    private var fileItemView: RecyclerView? = null
    private var emptyFileItemView: TextView? = null
    private var mCurrentFilesAdapter: FilesAdapter? = null

    override fun getResId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_work_space
    }

    private fun bindViews() {
        toolBar = findViewById(R.id.tool_bar)
        fileItemView = findViewById(R.id.rcv_current_files)
        emptyFileItemView = findViewById(R.id.tv_work_space_empty_view)
    }

    override fun onBaseBindView() {
        /*执行视图绑定*/
        bindViews()
        /** Set a Toolbar to act as the ActionBar for this Activity window.*/
        Lg.d(TAG, "preparing the toolbar>...")
        setSupportActionBar(toolBar)
        Lg.d(TAG, "initListViewAnim")
        initRecyclerViewAnim()

        //RecycleView的样式设置
        fileItemView?.layoutManager = GridLayoutManager(this, 1)
        fileItemView?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        /*实例化mPresenter对象,来充当操作Model和View的middle man*/
        mPresenter = WorkSpaceActivityPresenter(this)
        mPresenter?.start()
        mPresenter?.onEmptyView()
        if (AppUtil.isMPermission()) {
            Lg.d(TAG, "try to request the permissions...")
            if (ContextCompat.checkSelfPermission(
                    this@WorkSpaceActivity,
                    AppPermissions.permission_storage.get(0)
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this@WorkSpaceActivity,
                    AppPermissions.permission_storage.get(1)
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            } else {
                mPresenter?.onLoadMemoData()
            }
        } else {
            mPresenter?.onLoadMemoData()
        }
    }

    /*实现Presenter.View中的抽象方法*/
    /*载入数据*/
    override fun onLoadData() {
        mPresenter?.onLoadMemoData()
    }

    /*加载文件列表数据,同时使用动画加载*/
    private fun initRecyclerViewAnim() {
        Lg.d(TAG, "using the animation to load the file list>...")
        val animation = AnimationUtils.loadAnimation(this, R.anim.right_in)
        val controller = LayoutAnimationController(animation)
        /**
         * android.view.animation.LayoutAnimationController
         * public void setDelay(float delay)
        Sets the delay, as a fraction of the animation duration, by which the children's animations are offset.
        The general formula is:
        child animation delay = child index * delay * animation duration*/
        controller.delay = 0.3f
        /*Sets the order used to compute the delay of each child's animation.*/
        controller.order = 0
        /**
         * interpolator:插入器
         * android.view.animation.LayoutAnimationController
         * public void setInterpolator(android.view.animation.Interpolator interpolator)
        Sets the interpolator used to interpolate the delays between the children.
         */
        controller.interpolator = CubicInterpolator(0.47f, 0.01f, 0.44f, 0.99f)
        fileItemView!!.layoutAnimation = controller
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.work_space_menu, menu)
        Lg.d(TAG, "testing the menu create")
//        JudgeNew.isNew = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.menu_work_space_add_a_map -> {
                Lg.d(TAG,"testing the New Map item click")
                JudgeNew.isNew = true
                intentToEditMap()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 请求内存卡权限
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this@WorkSpaceActivity, AppPermissions.permission_storage,
            AppPermissions.request_permission_storage
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppPermissions.request_permission_storage) {
            if (AppUtil.verifyPermissions(grantResults)) {
                mPresenter?.onLoadMemoData()
            } else {
                Toast.makeText(this, "You denied the storage permission!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

//    override fun setPresenter(presenter: BaseWorkSpaceActivityPresenter.Presenter?) {
//        Lg.d(TAG, "you are setting the presenter of the WorkSpaceMangager")
//        mPresenter = presenter
//    }

    override fun showEmptyView() {
        emptyFileItemView!!.visibility = View.VISIBLE
        fileItemView!!.visibility = View.GONE
    }


    /*对文件列表进行编辑:*/
    override fun setListData(listData: ArrayList<FileLog>) {
        if (mCurrentFilesAdapter == null) {

            mCurrentFilesAdapter = FilesAdapter(this, listData)
            /*set the single click event:*/
            mCurrentFilesAdapter?.setRecyclerItemClickListener(
                object : RecyclerItemClickListener {
                    /*the concrete implement for the file item click event
                    * for the single click,it will turn to the edit activity:*/
                    override fun onItemClick(view: View, position: Int) {
                        /*get the file path ,the pass it to the intent*/
                        val path: String? = mPresenter?.getItemFilePath(position)
                        d(TAG,"path to open:=$path")
                        val name= File(path).name
                        d(TAG,"path to open:get name:$name")
                        JudgeNew.opened_name=name
                        JudgeNew.isNew=false
                        //跳转到Edit
                        /*不检查重名冲突*/
//                        JudgeNew.isNew=false
                        intentToEditMap(view, path)
                    }
                }
            )
            /*set the long Click event:remove the item or not:*/
            mCurrentFilesAdapter?.setRecycleItemLongClickListener(
                object : RecyclerItemLongClickListener {
                    /*implement the onItemLongClick interface*/
                    override fun onItemLongClick(view: View?, position: Int) {
                        /**
                         * androidx.appcompat.app.AlertDialog.Builder public Builder(@NonNull android.content.Context context)
                        Creates a builder for an alert dialog that uses the default alert dialog theme.
                         * */
                        val builder = AlertDialog.Builder(this@WorkSpaceActivity)
                        builder.setTitle(R.string.delete_title)
                        /**
                         * androidx.appcompat.app.AlertDialog.Builder
                         * public AlertDialog.Builder setPositiveButton(@StringRes int textId,
                        android.content.DialogInterface.OnClickListener listener)

                        Set a listener to be invoked when the positive button of the dialog is pressed.
                         */
                        builder.setPositiveButton(R.string.enter_sure) { _, _ ->
                            /*value-parameter <anonymous parameter 1>: Int*/
                            mPresenter?.removeItemFile(position)
                            mCurrentFilesAdapter?.notifyDataSetChanged()
                        }
                        builder.setNegativeButton(
                            R.string.cancel
                        ) { _, _ -> }
                        builder.setCancelable(true)
                        builder.create().show()
                    }
                })
            fileItemView!!.adapter = mCurrentFilesAdapter
        }
        if (listData.size > 0) {
            fileItemView!!.visibility = View.VISIBLE
            emptyFileItemView!!.visibility = View.GONE
        }
        mCurrentFilesAdapter?.notifyDataSetChanged()
        Log.i(
            TAG,
            "setListData: notifyDataSetInvalidation"
        )
    }

    override fun refreshListData() {
        mCurrentFilesAdapter?.notifyDataSetChanged()
    }

    override fun getTestDefaultPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath + AppConstants.test_maps
//        TODO("Not yet implemented")
    }

    private fun intentToEditMap() {
        val editIntent = Intent(this@WorkSpaceActivity, EditMapActivity::class.java)
        startActivity(editIntent)
    }

    /**
     * from file item to edit the correspond memo file (edit map activity)
     * @param view
     * @param filePath*/
    fun intentToEditMap(view: View, filePath: String?) {
        val transIntent = Intent(this@WorkSpaceActivity, EditMapActivity::class.java)
        var transitionActivityOptions: ActivityOptions? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this@WorkSpaceActivity,
                Pair.create(view, getString(R.string.trans_item))
            )
        }
        val uri = Uri.parse(filePath)
        transIntent.data = uri
        if (transitionActivityOptions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(transIntent, transitionActivityOptions.toBundle())
        } else {
            startActivity(transIntent)
        }
    }


    override fun onResume() {
        super.onResume()
        mPresenter?.onLoadMemoData()
    }

}