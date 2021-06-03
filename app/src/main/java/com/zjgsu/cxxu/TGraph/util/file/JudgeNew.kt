package com.zjgsu.cxxu.TGraph.util.file

object JudgeNew {
    var isNew=true
    var is_save_dialog=false
    var opened_name=""
    /**
     * 利用对立事件:经过各种情况的比较,发现不需要比较冲突的情况只有:通过打开已有文件来编辑,同时保存时的文件名和打开时的仍然一致,那么认为不用做文件名冲突检查
     * 这样,我们需要做的就是再打开导图时获取文件名name_open
     * 再将保存文件时输入的文件名name_save和name_open做对比即可
     *
     * 您可以在触发保存界面的dialog中将获取的输入(文件名)提取,并且和打开的文件的文件名作比较
     * 笔记name_open和name_save是否相等,如果是,就不检查冲突,此时将JudgeNew.isNew改为false*/

}