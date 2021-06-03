package com.zjgsu.cxxu.TGraph.util

import com.zjgsu.cxxu.TGraph.Lg

/**
 * 该类为微件的拖动操作提供支持
 * @param <T>
*/
class DragFlagSupporter<T>(private val loopBodyArray: Array<T>, private val mListener: LooperListener<T>?) {
    private val TAG: String = "DragFlagSupporter"
    private var point = 0

    /**
     * 定义一个内嵌于LooperFlag的接口*/
    interface LooperListener<T> {
        fun onLooper(item: T)
    }

    /*泛型实化*/
//    inline fun <reified T> getGenericType() = T::class.java
    /*获取下一个T对象*/
    fun next(): T {
        point += 1
        if (point == loopBodyArray.size) {
            point = 0
            Lg.d(TAG, "running the next()")
        }
        Lg.d(TAG, "test the parameter of the type LooperListener")
        mListener?.onLooper(loopBodyArray[point])
        /*返回T类型对象*/
        return loopBodyArray[point]
    }
}