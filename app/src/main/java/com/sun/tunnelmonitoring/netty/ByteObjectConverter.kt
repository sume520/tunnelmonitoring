import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object ByteObjectConverter {
    fun objectToByte(obj: Any?): ByteArray {
        var bytes: ByteArray? = null
        var byteout = ByteArrayOutputStream()
        var objectOut = ObjectOutputStream(byteout)
        try {
            objectOut.writeObject(obj)
            bytes = byteout.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                byteout.close()
                objectOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (bytes == null)
            throw Exception("对象转换字节数组失败")
        return bytes
    }

    fun byteToObject(bytes: ByteArray?): Any {
        var obj: Any? = null
        var bytein: ByteArrayInputStream? = null
        var objectIn: ObjectInputStream? = null
        try {
            bytein = ByteArrayInputStream(bytes)
            objectIn = ObjectInputStream(bytein)
            obj = objectIn.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bytein!!.close()
                objectIn!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (obj == null)
            throw Exception("字节数组转对象失败")
        return obj
    }
}