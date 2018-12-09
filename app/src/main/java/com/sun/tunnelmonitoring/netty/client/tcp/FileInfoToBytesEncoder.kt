import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class FileInfoToBytesEncoder: MessageToByteEncoder<FileInfo>(){
    override fun encode(p0: ChannelHandlerContext?, fileinfo: FileInfo?, out: ByteBuf?) {
        var bytes=ByteObjectConverter.objectToByte(fileinfo)
        val delimiter="&^%~".toByteArray()
        bytes=bytes+delimiter
        out!!.writeBytes(bytes)
        p0!!.flush()
    }
}