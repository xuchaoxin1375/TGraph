package com.zjgsu.cxxu.TGraph.util.file

import kotlin.jvm.JvmStatic

class FileInformation {
    @JvmField
    var android_version: String? = null
    @JvmField
    var map_name: String? = null
    @JvmField
    var date: String? = null
    override fun toString(): String {
        return "FileInformation{" +
                "android_version='" + android_version + '\'' +
                ", map_name='" + map_name + '\'' +
                ", date='" + date + '\'' +
                '}'
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val fileInfo= FileInformation()
            fileInfo.android_version = "android5.0"
//            fileInfo.app_version = "1.0.1"
            fileInfo.date = "2021"
            println(fileInfo.toString())
        }
    }
}