package com.zjgsu.cxxu.TGraph.util

import android.content.Context
import android.util.TypedValue

/**
 * 像素相关单位转换 工具类
 */
object DensityUtils {
    /**
     * dp密度无关像素转px像素
     */
    @JvmStatic
    fun dp2px(context: Context, dpVal: Float): Int {
        /**将保存维度的未打包复杂数据值转换为其最终浮点值。
         * 这涉及到手势灵敏度
         * Converts an unpacked complex data value holding a dimension to its final floating point value. The two parameters unit and value are as in TYPE_DIMENSION.
         * use the toInt() Converts this Float value to Int.
         */
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal, context.resources
                .displayMetrics
        ).toInt()
    }

    /**
     * sp可缩放像素 (sp)转px
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spVal, context.resources
                .displayMetrics
        ).toInt()
    }

    /**
     * px转dp
     */
    fun px2dp(context: Context, pxVal: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxVal / scale).toInt()
    }

    /**
     * px转sp
     */
    fun px2sp(context: Context, pxVal: Float): Float {
        val result = (pxVal / context.resources.displayMetrics.scaledDensity).toInt()
        return result.toFloat()
    }
}