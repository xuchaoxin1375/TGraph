package com.zjgsu.cxxu.TGraph.mvp.view

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.base.BaseAlertDialog
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.util.AppUtil.getFileList
import com.zjgsu.cxxu.TGraph.util.AppUtil.hideKeyboard
import com.zjgsu.cxxu.TGraph.util.StringTool.showToast
import com.zjgsu.cxxu.TGraph.util.file.JudgeNew

class EditAlertDialog constructor(context: Context) : BaseAlertDialog(context, R.style.DivDialog) {
    companion object {
        private const val TAG = "EditAlertDialog"
    }

    private lateinit var mDialogTitle: TextView
    private lateinit var mDialogInput: EditText
    private lateinit var mDialogBtnEnter: Button
    private lateinit var mDialogBtnDelete: Button
    private lateinit var mDialogInputClear: ImageView
    private lateinit var mDialogHadSameiFle: TextView
//    private lateinit var mDialogCheckState: RelativeLayout
    private lateinit var mDialogCheckState: TextView

    private var mConditonDeleteTextValue: String? = null
    private var mEnterCallBack: EnterCallBack? = null
    private var mDeleteCallBack: DeleteCallBack? = null
    private var mNodeModel: Node<String>? = null
    private var mFileLists: List<String>? = null
    override fun onBaseBindView() {
        Lg.d(TAG, "running the onBaseBindView()..")
        mDialogTitle = findViewById(R.id.dialog_edit_tv_title) as TextView
        mDialogInput = findViewById(R.id.dialog_edit_et_input) as EditText
        mDialogBtnEnter = findViewById(R.id.dialog_btn_enter) as Button
        mDialogBtnDelete = findViewById(R.id.dialog_btn_delete) as Button
        mDialogInputClear = findViewById(R.id.dialog_edit_iv_input_clear) as ImageView
//        doing testing..
        mDialogCheckState = findViewById(R.id.dialog_edit_et_input) as TextView
        mDialogHadSameiFle = findViewById(R.id.dialog_edit_tv_had_same_file) as TextView
        mDialogBtnEnter.setOnClickListener(View.OnClickListener {
            Lg.d(TAG, "checking the save instruction...")
            d(TAG, "checking the mEnterCallBack: $mEnterCallBack\n"+(mEnterCallBack!=null))

//            getFileList("/storage/emulated/0/TestMaps/")
            if (mEnterCallBack != null) {
                val inputStr = input.toString()
                d(TAG, "checking the input text...:$inputStr")


                if (mFileLists != null) {
                    for (str in mFileLists!!) {
//                        d(TAG,"fileName:in checking:"+str)
                    }
                    d("value:", "inputStr:$inputStr.memo")
                    d(TAG,"JudgeNew.isNew =${JudgeNew.isNew }")
                    val bool=JudgeNew.isNew and mFileLists!!.contains(inputStr+".memo")
                    d(TAG,"judge the bool=$bool" )
                    /*显示编辑框*/
                    mDialogHadSameiFle.visibility = View.VISIBLE

                    //同名文件提示更改
                    mFileLists=getFileList("/storage/emulated/0/TestMaps/")
                    var is_same_name=(JudgeNew.opened_name== "$inputStr.memo")
                    val bool1= is_same_name and (JudgeNew.is_save_dialog) and (!JudgeNew.isNew)
                    d(TAG,"field1:$is_same_name")//输入内容是否为打开文件的文件名
                    d(TAG,"field2:${JudgeNew.is_save_dialog}")//在保存界面获取的输入的内容(文件名)
                    d(TAG,"field3:${!JudgeNew.isNew}")//true表示是从文件列表中打开的
                    d(TAG,"以上三项全true时,可以跳过重名检查..:")
                    d(TAG,"bool1=${bool1}")
                    //重置:
//                    JudgeNew.opened_name=""
//                    JudgeNew.isNew = !bool1
                    when {

                        (!bool1) and mFileLists!!.contains(inputStr+".memo") -> {
                            "rename the file please.".showToast()
                            mDialogHadSameiFle.text = context.resources.getString(R.string.same_name_file)
                            /**
                             * Abstraction for an Animation that can be applied to Views, Surfaces, or other objects.
                             * Constructor to use when building a TranslateAnimation from code
                             */
                            Lg.d(TAG, "test the shake..")
                            val shake: Animation = TranslateAnimation(0f, 10f, 0f, 0f)
                            shake.duration = 1000

                            shake.interpolator = CycleInterpolator(7f)
                            mDialogCheckState.startAnimation(shake)
                            return@OnClickListener
                        }
                        TextUtils.isEmpty(inputStr) -> {
//                            dialogHadSameiFle.visibility = View.VISIBLE
                            mDialogHadSameiFle.text = context.resources.getString(R.string.file_name_empty)
                            val shake: Animation = TranslateAnimation(0f, 10f, 0f, 0f)
                            shake.duration = 1000

                            shake.interpolator = CycleInterpolator(7f)
                            mDialogCheckState.startAnimation(shake)
                            return@OnClickListener
                        }
                        else -> {
                            mDialogHadSameiFle.visibility = View.INVISIBLE
                        }
                    }
//                    JudgeNew.is_save_dialog=false
                }
//                JudgeNew.isNew=false
                mEnterCallBack!!.onEdit(inputStr)
                hideKeyboard(context, mDialogInput)
            }
//            JudgeNew.isNew=false
        })
        mDialogInputClear.setOnClickListener { setInput("") }
        mDialogBtnDelete.setOnClickListener {
            if (mDeleteCallBack != null) {
                if (mDialogTitle.text === this@EditAlertDialog.context
                        .resources.getString(R.string.save_file)
                ) {
                    mDeleteCallBack!!.onDelete()
                } else if (mNodeModel != null) {
                    //根节点不能进行删除
                    val parentNode = mNodeModel!!.parentNode
                    if (parentNode != null) {
                        mDeleteCallBack!!.onDeleteModel(mNodeModel)
                    }
                }
            }
            dismiss()
            hideKeyboard(context, mDialogInput)
        }
    }

    fun setDivTitle(title: String?) {
        this.mDialogTitle.text = title
    }

    /*This is the interface for text whose content and markup can be changed (as opposed to immutable text like Strings). If you make a DynamicLayout of an Editable, the layout will be reflowed as the text is changed.*/
    private val input: Editable
        get() = mDialogInput.text

    fun setInput(value: String?) {
        mDialogInput.setText(value)
    }


    fun setConditionDeleteTextValue(conditionDeleteTextValue: String?) {
        mConditonDeleteTextValue = conditionDeleteTextValue
        mDialogBtnDelete.text = conditionDeleteTextValue
    }

    /*将文件列表赋值给mFileList*/
    fun setFileLists(fileLists: List<String>?) {
        this.mFileLists = fileLists
    }

    fun setNodeModel(nodeModel: Node<String>) {
        this.mNodeModel = nodeModel
        mDialogBtnDelete.isEnabled = nodeModel.parentNode != null
    }

    fun addEnterCallBack(callBack: EnterCallBack?) {
        mEnterCallBack = callBack
    }

    fun addDeleteCallBack(callBack: DeleteCallBack?) {
        mDeleteCallBack = callBack
    }

    fun clearInput() {
        setInput("")
    }

    interface EnterCallBack {
        fun onEdit(value: String?)
    }

    interface DeleteCallBack {
        fun onDeleteModel(nodeModel: Node<String>?)
        fun onDelete()
    }
}