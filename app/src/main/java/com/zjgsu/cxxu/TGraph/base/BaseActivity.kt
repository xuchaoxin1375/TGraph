package com.zjgsu.cxxu.TGraph.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zjgsu.cxxu.TGraph.Lg

/**
 * 本类不不基于具体的布局,但是为各个Activity的创建做了预处理工作
 * 同时提供了一定的调试便利
 * abstract 类本身就是为了让其他类来继承它的(no need explicit "open" mark)*/

abstract class BaseActivity() : AppCompatActivity() {
    companion object {
        private const val TAG = "FromBaseActivity"
    }

    /*you can define(override) this method as you like:
    *   you may in there to execute the setContentView() which we often write in the specified Activity class
    *and set a abstract to get the context resId then pass it to the setContentView
    *   what's more ,you can still write this part in each activity by use super.onCreate(savedInstanceState)
    * like the classic standard writing:
*     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*get the activity name:*/
        Lg.d(TAG, "ActivityName:" + javaClass.simpleName)
//        onBaseIntent()
//        onBasePreLayout()
        /**
         * the setContentView():
        android.app.Activity Set the activity content from a layout resource. The resource will be inflated, adding all top-level views to the activity.*/
        setContentView(getResId(savedInstanceState))
        onBaseBindView()
        onLoadData()
    }


    /**
     * 返回布局文件(id)
     *
     * @return id
     */
    abstract fun getResId(savedInstanceState: Bundle?): Int

    /**在onBaseBindView()中集中处理视图（控件）绑定操作：
     * 这是为传统的绑定方式设计的接口
     * 可以这样子组织代码：
     * 在某个特定的Activity类中，将一系列的findViewById组织到一个名为bindView()的方法中，然后做统一的调用
     * 因为，我们经常会为可视控件设置点击事件的监听，你可以将视图的绑定和对应的点击事件的监听函数一同编入该接口的是实现*/
    abstract fun onBaseBindView()

    /**
     * 加载数据
     */
    open fun onLoadData() {}
}
