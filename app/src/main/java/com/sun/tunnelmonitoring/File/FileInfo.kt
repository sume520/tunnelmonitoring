import java.io.Serializable
import java.security.MessageDigest

data class FileInfo(var filename: String, var filesize: Long, var filedata: ByteArray) : Serializable {
    fun getMD5(): ByteArray? {
        val md = MessageDigest.getInstance("MD5")
        val md5 = md.digest(filedata)
        return md5
    }

    override fun equals(other: Any?): Boolean {
        return (other as FileInfo).getMD5()!!.contentEquals(this.getMD5()!!)
    }

    override fun toString(): String {
        return "文件名：${filename}\n文件大小：${filesize / 1024}Kb\nMD5：${getMD5()!!.joinToString(" ")}"
    }
}