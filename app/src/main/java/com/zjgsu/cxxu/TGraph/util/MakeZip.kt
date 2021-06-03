package com.zjgsu.cxxu.TGraph.util

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object MakeZip {
    private var arrayFiles: MutableList<File>? = null
    private var sourcePath: String? = null
    private var saveFilePath: File? = null

    @JvmStatic
    fun create(sourcePath: String?, savePath: File?) {
        //该容器可以保存任何类型对象
        arrayFiles = ArrayList()
        this.sourcePath = sourcePath
        saveFilePath = savePath

        //获取文件夹下的文件
        findAllFiles(sourcePath)
        //生产.zip
        makeZipFile((arrayFiles as ArrayList<File>).toTypedArray(), saveFilePath)
    }

    /**
     * 找到所有输入文件夹路径下的所有文件
     * 将搜索结果保存在工具单例MakeZip的容器成员变量中
     *
     * @param filePath
     */
    @JvmStatic
    private fun findAllFiles(filePath: String?) {
        if (filePath == null || filePath.trim().isEmpty()) {
            println("请输入文件路径！")
            return
        }
        val sourceFile = File(filePath)
        if (!sourceFile.exists()) {
            println("输入的文件路径不存在！")
            return
        } else {
            /*找到某个文件/目录*/
            //如果是个目录:遍历该目录下所有的文件,递归执行
            if (sourceFile.isDirectory) { //文件夹
                val subFiles = sourceFile.listFiles()
                for (f in subFiles) {
                    findAllFiles(f.absolutePath)
                }
            } else {
                //如果是个文件
                println("找到文件： ${sourceFile.absoluteFile}\t")
                //将文件添加到容器中
                arrayFiles!!.add(sourceFile)
            }
        }
    }

    @JvmStatic
    private fun makeZipFile(files: Array<File>, zipNameFile: File?) {
        /**
         * A file output stream is an output stream for writing data to a File or to a FileDescriptor.
        there the fos:FileOutputStream will declare a reference variable,waiting for refenrece a specific instance of FileOutputStream
         */
//        val fos: FileOutputStream
        try {
            val fos = FileOutputStream(zipNameFile)
            /*Creates a new ZIP output stream:*/
            val zos = ZipOutputStream(fos)
            /*get the buffer array*/
            val buffer = ByteArray(1024)
            for (file in files) {
                //截取前缀
                val cutPath = file.absolutePath.substring(sourcePath!!.length)
                println("entry:$cutPath")
                /*Creates a new zip entry with the specified name.*/
                val entry = ZipEntry(cutPath)
                val fis = FileInputStream(file)
                zos.putNextEntry(entry)
                var read = 0
                while (fis.read(buffer).also { read = it } != -1) {
                    /*写出文件*/
                    zos.write(buffer, 0, read)
                }
                zos.closeEntry()
                fis.close()
            }
            zos.close()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    /*test the function:*/
//    open fun main() {
//        val sourcePath = "/Users/test/Desktop/create_file"
//        val client = MakeZip()
//        val saveFile = File("/Users/test/Desktop/myplan.test")
//        client.create(sourcePath, saveFile)
//    }

    /*test the function:*/
    @JvmStatic
    fun main(args: Array<String>) {
        val sourcePath = "C:/users/cxxu/desktop/."
        val saveFile = File("C:/users/cxxu/desktop/myplan.zip")
        create(sourcePath, saveFile)

    }

}

