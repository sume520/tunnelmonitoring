import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import java.io.File

class TcpServerInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(p0: SocketChannel?) {
        val delimiter = "&^%~".toByteArray()
        var pipline = p0!!.pipeline()
        pipline.addLast(DelimiterBasedFrameDecoder(1024 * 1024 * 30, Unpooled.copiedBuffer(delimiter)))
            .addLast("decoder", FileInfoDecoder())
            .addLast(TcpServerHandler())

    }

    class TcpServerHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)

            var fileinfo = msg as FileInfo
            var file = File("E:/${fileinfo.filename}")
            if (!file.exists()) file.createNewFile()
            file.writeBytes(fileinfo.filedata)
            println("接收数据完成")
            println(fileinfo.toString())
        }
    }

    class FileInfoDecoder : ByteToMessageDecoder() {
        override fun decode(ctx: ChannelHandlerContext?, inbuf: ByteBuf?, list: MutableList<Any>?) {
            var readable = inbuf!!.readableBytes()
            if (readable == 0)
                throw Exception("读取到的字节为0")
            var bytes = ByteArray(readable)
            inbuf.readBytes(bytes)
            var fileinfo = ByteObjectConverter.byteToObject(bytes) as FileInfo
            list!!.add(fileinfo)
        }
    }
}