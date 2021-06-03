package com.zjgsu.cxxu.TGraph.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.Lg.d
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    private const val TAG = "AppUtil"

    /**
     * 隐藏键盘
     */
    @JvmStatic
    fun hideKeyboard(context: Context, view: View) {
        /**
        首先需要一个Manager对通知进行管理，可以通过调用Context的getSystemService()方法获取
        Return the handle to a system-level service by name.
        The class of the returned object varies by the requested name.
        you should cast the type as a correct type you want
         * */
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        /*Synonym for hideSoftInputFromWindow(IBinder, int, ResultReceiver) without a result: request to hide the soft input window from the context of the window that is currently accepting input.*/
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    /**
     * 显示Toast的信息
     *
     * @param mContext
     * @param toastInfo
     */
    @JvmStatic
    fun showToast(
        mContext: Context? = GetContextApplication.context,
        toastInfo: String?,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        val mToast = Toast.makeText(mContext, toastInfo, duration)
        mToast.show()
    }


    @JvmStatic
    val androidSystemVersion: String
        get() = "android " + Build.VERSION.SDK_INT

    fun transferLongToDate(dateFormat: String?, millSec: Long?): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.CHINA)
        val date = Date(millSec!!)
        return sdf.format(date)
    }

    /**
     * 可以这样判断判断是否为android 6.0+
     *
     * @return true or false
     */
//    val isMPermission: Boolean
//        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            true
//        } else {
//            false
//        }
    open fun isMPermission(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M


    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value [PackageManager.PERMISSION_GRANTED].
     *
     * @see Activity.onRequestPermissionsResult
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty()) {
            return false
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun getFileList(path: String): ArrayList<String> {
        d(TAG,"getting the files...")
//        print("getting the files...")
        val dir = File(path)
        val fileNames = dir.listFiles()
        d(TAG,"the size of the fileList:${fileNames.size}")
        val list = ArrayList<String>()
        for (fileName in fileNames) {
            Lg.d(TAG, "fileName:${fileName.name}")
            list.add(fileName.name)
        }
        return list
    }
}
//fun main(){
//   val str=AppUtil.transferLongToDate()
//
//}