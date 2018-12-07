package com.sun.tunnelmonitoring.File

import java.io.Serializable

data class FileInfo(var filename: String, var filesize: Int, var filedata: ByteArray) : Serializable