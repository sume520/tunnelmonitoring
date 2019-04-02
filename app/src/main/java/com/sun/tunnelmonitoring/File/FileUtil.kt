package com.sun.tunnelmonitoring.File

import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import java.io.File
import java.io.FileNotFoundException

var handler = Handler()

fun readFile(filename: String): ByteArray? {
    var filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename
    var data: ByteArray? = null
    try {
        var file = File(filename)
        data = file.readBytes()
        Log.d("FileUtil", "已读取文件${filename},文件内容为：${data}，大小为：${data.size}")
    } catch (e: FileNotFoundException) {
        Log.e("FileUtil", "找不到文件")
    }
    return data
}

fun writeFile(filename: String, data: ByteArray) {
    Thread {
        val filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename
        val file = File(filename)
        if (!file.exists()) {
            file.createNewFile()
            Log.d("FileUtil", "文件 $filename 不存在，创建新文件")
        }
        try {
            file.writeBytes(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

fun createFile(filename: String) {
    if (filename.isBlank())
        Toast.makeText(MyApplication.getContext(), "文件名不能为空", Toast.LENGTH_SHORT).show()
    else {
        val file = File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename)
        if (file.exists())
            Toast.makeText(MyApplication.getContext(), "文件已存在", Toast.LENGTH_SHORT).show()
        else {
            file.createNewFile()
            Toast.makeText(MyApplication.getContext(), "文件 ${filename} 创建完成", Toast.LENGTH_SHORT).show()
        }
    }
}

fun saveToFile(bytes: ByteArray, filename: String): Boolean {
    //保存到sd卡根目录rec.txt文件中
    val filepath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "$filename.txt"
    val file = File(filepath)
    //检测文件是否存在
    if (!file.exists()) file.createNewFile()
    try {
        file.writeBytes(bytes)
        return true
    }catch (e:Exception){
        return false
    }

}
