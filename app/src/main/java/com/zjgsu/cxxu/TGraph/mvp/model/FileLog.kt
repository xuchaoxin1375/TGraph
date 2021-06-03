package com.zjgsu.cxxu.TGraph.mvp.model

class FileLog {

    //修改时间
   lateinit var editTime: String

    //文件路径
    lateinit var filePath: String

    //导图标题(将导图的根节点内容抽取为文件名(标题))
    lateinit var mapRoot: String
}