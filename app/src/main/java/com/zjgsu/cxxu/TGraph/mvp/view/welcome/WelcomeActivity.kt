package com.zjgsu.cxxu.TGraph.mvp.view.welcome

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.base.BaseActivity
import com.zjgsu.cxxu.TGraph.constants.AppPermissions
import com.zjgsu.cxxu.TGraph.mvp.view.workspace.WorkSpaceActivity
import com.zjgsu.cxxu.TGraph.util.AppUtil

/*欢迎界面Activity也经常被命名作splashActivity*/
class WelcomeActivity : BaseActivity() {
    companion object {
        private const val TAG = "WelcomeActivity"

    }

    override fun getResId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_splash
    }

    override fun onBaseBindView() {
        if (AppUtil.isMPermission()) {
            if (ContextCompat.checkSelfPermission(
                    this@WelcomeActivity,
                    AppPermissions.permission_storage[0]
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this@WelcomeActivity,
                    AppPermissions.permission_storage.get(1)
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            } else {
                intentToWorkSpace()
            }
        } else {
            intentToWorkSpace()
        }
    }

    private fun intentToWorkSpace() {
        /* SAM optimal work:
        Java 8 introduced lambda expressions along with functional interfaces.
         A functional interface is an interface with a single method.
         They are commonly referred to as single abstract method or SAM.*/
        Thread {
            try {
                Thread.sleep(500)
                val intent = Intent(this, WorkSpaceActivity::class.java)
//                val intent=Intent(this, MainActivity::class.java)
                Lg.d(TAG, "befor Turning to the WorkSpace activity...")
                startActivity(intent)

                Lg.d(TAG, "after  the WorkSpace activity...")
                this.finish()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    /*empty implementation of the onLoadData()*/
    override fun onLoadData() {}

    /**
     * 请求内存卡权限
     */
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this@WelcomeActivity, AppPermissions.permission_storage,
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
                intentToWorkSpace()
            } else {
                Toast.makeText(this, "the permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}