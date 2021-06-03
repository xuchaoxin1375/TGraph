package com.zjgsu.cxxu.TGraph.constants

import android.Manifest

object AppPermissions {
    //权限请求
    var request_permission_storage = 2

    var permission_storage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}