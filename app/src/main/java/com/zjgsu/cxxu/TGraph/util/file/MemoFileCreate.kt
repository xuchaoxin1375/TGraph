package com.zjgsu.cxxu.TGraph.util.file

import Tree
import android.os.Environment
import android.util.Log
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.constants.AppConstants
import com.zjgsu.cxxu.TGraph.util.MakeZip.create
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 *
 * 1.在TestMaps里创建一个temp_create_file文件夹;
 * 2.content文件进行写序列化的tree对象;
 * 3.创建一个fileInfo.txt文件进行保存文件基本信息,修改日期,文件名,系统的版本;
 * 4.为file_name_temp和fileInfo.txt进行压缩为导图文件;
 * 5.删除create_file文件夹下的所有文件;
 */
class MemoFileCreate {
    fun createTestMapsDirectory() {
        if (hasSDCard()) {
            val map_path = Environment.getExternalStorageDirectory().path + AppConstants.test_maps
            val testMapDirectory = File(map_path)
            if (!testMapDirectory.exists()) {
                val mkdir = testMapDirectory.mkdirs()
                Log.i(TAG, "创建testmaps文件路径:" + mkdir + testMapDirectory.absolutePath)
            }
        } else {
            Log.e(TAG, "createTestMapsDirectory: 没有内存卡!")
        }
    }

    fun createTempDirectory() {
        if (hasSDCard()) {
            val path =
                Environment.getExternalStorageDirectory().path + AppConstants.test_maps + AppConstants.test_create_file
            val testMapDirectory = File(path)
            if (!testMapDirectory.exists()) {
                testMapDirectory.mkdirs()
                Log.i(TAG, "创建Temp文件夹:$path")
            }
        } else {
            Log.e(TAG, "createTempDirectory: 没有内存卡!")
            return
        }
    }

    fun writeContent(obj: Any) {
        try {
            val content_path = Environment.getExternalStorageDirectory().path +
                    AppConstants.test_maps +
                    AppConstants.test_create_file + "content"
            writeTreeObject(content_path, obj)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun writeLog(fileInfo: FileInformation) {
        try {
            val fileInfo_path = Environment.getExternalStorageDirectory().path +
                    AppConstants.test_maps +
                    AppConstants.test_create_file + "fileInfo.txt"
            writeFile(fileInfo_path, fileInfo.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun makeTestFile(saveName: String) {
        val temp_path = Environment.getExternalStorageDirectory().path +
                AppConstants.test_maps + AppConstants.test_create_file
        var savePath = Environment.getExternalStorageDirectory().path +
                AppConstants.test_maps + saveName
        if (!savePath.endsWith(".memo")) {
            savePath = "$savePath.memo"
        }
        val saveFile = File(savePath)
        create(temp_path, saveFile)
        d(TAG, saveFile.toString())
        Log.i(TAG, "创建memo文件成功$savePath")
    }

    fun deleteTemp() {
        val temp_path = (Environment.getExternalStorageDirectory().path
                + AppConstants.test_maps +
                AppConstants.test_create_file)
        val file = File(temp_path)
        delete(file)
    }

    fun readFileInfo(zipFilePath: String): String {
        return readZipFile(zipFilePath, AppConstants.fileInfo)
    }

    @Throws(ClassNotFoundException::class, InvalidClassException::class)
    fun readContentObject(zipFilePath: String): Any? {
        var o: Any? = null
        o = readZipFileObject(zipFilePath, AppConstants.content)
        return o
    }

    private fun delete(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }
        if (file.isDirectory) {
            val childFiles = file.listFiles()
            if (childFiles == null || childFiles.size == 0) {
                file.delete()
                return
            }
            for (i in childFiles.indices) {
                delete(childFiles[i])
            }
            file.delete()
        }
    }

    @Throws(IOException::class)
    private fun writeTreeObject(filePath: String, obj: Any) {
        val fos = FileOutputStream(filePath)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(obj)
        oos.close()
    }

    @Throws(IOException::class, ClassNotFoundException::class, InvalidClassException::class)
    fun readTreeObject(filePath: String?): Tree<String> {
        val tree: Tree<String>
        val fos = FileInputStream(filePath)
        val ois = ObjectInputStream(fos)
        tree = ois.readObject() as Tree<String>
        return tree
    }

    @Throws(IOException::class)
    private fun writeFile(path: String, fileContext: String) {
        val fos = FileOutputStream(path)
        fos.write(fileContext.toByteArray(charset("iso8859-1")))
        fos.close()
    }

    @Throws(IOException::class)
    private fun readFile(path: String): String {
        /*创建字节输入流对象*/
        val fis = FileInputStream(path)
        val buf = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var len = 0
        while (fis.read(buf).also { len = it } != -1) {
            baos.write(buf, 0, len)
        }
        fis.close()
        baos.close()
        return baos.toString()
    }

    private fun hasSDCard(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    @Throws(ClassNotFoundException::class, InvalidClassException::class)
    private fun readZipFileObject(filePath: String, fileName: String): Any? {
        var inputStream: InputStream? = null
        var zipFile: ZipFile? = null
        var objectInputStream: ObjectInputStream? = null
        var targetObject: Any? = null
        try {
            val file = File(filePath)
            zipFile = ZipFile(file)
            val zipEntry = ZipEntry(fileName)
            inputStream = zipFile.getInputStream(zipEntry)
            objectInputStream = ObjectInputStream(inputStream)
            targetObject = objectInputStream.readObject()
            zipFile.close()
            inputStream.close()
            objectInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return targetObject
    }

    private fun readZipFile(filePath: String, fileName: String): String {
        val buffer = StringBuffer()
        var inputStream: InputStream? = null
        var zipFile: ZipFile? = null
        try {
            val file = File(filePath)
            zipFile = ZipFile(file)
            val zipEntry = ZipEntry(fileName)
            inputStream = zipFile.getInputStream(zipEntry)
            val bytes = ByteArray(1024)
            var len: Int
            while (inputStream.read(bytes).also { len = it } != -1) {
                buffer.append(String(bytes, 0, len))
            }
            inputStream.close()
            zipFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return buffer.toString()
    }

    companion object {
        private const val TAG = "MemoFileCreate"
    }
}