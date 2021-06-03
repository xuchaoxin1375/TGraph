package com.zjgsu.cxxu.TGraph.fileAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.mvp.view.workspace.RecyclerItemLongClickListener
import com.zjgsu.cxxu.TGraph.mvp.view.workspace.RecyclerItemClickListener
import com.zjgsu.cxxu.TGraph.mvp.model.FileLog


/**
 * 定义用于将您的数据与 ViewHolder 视图相关联的 Adapter。
 * 本类为文件列表所用到的recyclerView的Adapter:
 * 然而,您应当注意,本app要要显示的数据是和文件相关的(随着app的使用的文件数目会变化,所以您应当借助其他对象来可更新地获取数据)
 ** 注意该类需要继承自RecyclerView.androidx.recyclerview.widget.RecyclerView.Adapter<VH extends RecyclerView.ViewHolder> public Adapter()
 * 实现 Adapter 和 ViewHolder。
这两个类配合使用，共同定义数据的显示方式。
adapter 接受需要被显示时的数据(往往是一个数组/列表),其他参数可选,比如Context*/

class FilesAdapter(
    private var mContext: Context,
    private var mLists: ArrayList<FileLog>
) : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {
    companion object {
        private const val TAG = "FilesAdapter"
    }

    private var mRecycleItemClickListener: RecyclerItemClickListener? = null
    private var mRecycleItemLongClickListener: RecyclerItemLongClickListener? = null
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): FileViewHolder {
        /**
         * android.view public abstract class LayoutInflater extends Object

         * Instantiates a layout XML file into its corresponding ItemView objects.
         * It is never used directly.
         * Instead, use android.app.Activity.getLayoutInflater() or Context.getSystemService to retrieve a standard LayoutInflater instance that is already hooked up to the current context and correctly configured for the device you are running on.
        To create a new LayoutInflater with an additional LayoutInflater.Factory for your own views, you can use cloneInContext to clone an existing ViewFactory, and then call setFactory on it to include your Factory.*/
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_current_file, viewGroup, false)
        return FileViewHolder(
            view,
            mRecycleItemClickListener,
            mRecycleItemLongClickListener
        )
    }

    /*bind view with data*/
    override fun onBindViewHolder(
        holder: FileViewHolder,
        position: Int
    ) {
        val fileModel: FileLog = mLists[position]
        Lg.d(TAG, "check the mapRoot:${fileModel.mapRoot}")
        holder.mRootValue.text = fileModel.mapRoot
        holder.mFilePath.text = fileModel.filePath
    }

    override fun getItemCount(): Int {
        return mLists.size
    }

    /*the method provides for the workSpaceActivity to invoke*/
    fun setRecyclerItemClickListener(recycleItemClickListener: RecyclerItemClickListener?) {
        mRecycleItemClickListener = recycleItemClickListener
        Lg.i(TAG, "your  operation invoked the setRecyclerItemClickListener():$mRecycleItemClickListener")
    }

    fun setRecycleItemLongClickListener(recycleItemLongClickListener: RecyclerItemLongClickListener?) {
        mRecycleItemLongClickListener = recycleItemLongClickListener
        Lg.i(TAG, "your  operation invoked the setRecyclerItemClickListener():$mRecycleItemLongClickListener")
    }

    class FileViewHolder(
        itemView: View,
        listener: RecyclerItemClickListener?,
        longListener: RecyclerItemLongClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        /*define what views are encapsulate in the ViewHolder */
        val mRootValue: TextView = itemView.findViewById<View>(R.id.test_file_root_value) as TextView

        //        Lg.i(TAG,"watch the R.id.test_file")
        val mFilePath: TextView = itemView.findViewById<View>(R.id.test_file_path) as TextView

        /*define the  listener function of the RecyclerItem in the ViewHolder inner class */
        private val mListener: RecyclerItemClickListener? = listener
        private val mLongClickListener: RecyclerItemLongClickListener? = longListener

        /*(class initializer)*/
        init {
            itemView.setOnClickListener {
                this.mListener?.onItemClick(itemView, adapterPosition)
            }
            itemView.setOnLongClickListener {
                mLongClickListener?.onItemLongClick(itemView, adapterPosition)
                false
            }
        }
    }


}
