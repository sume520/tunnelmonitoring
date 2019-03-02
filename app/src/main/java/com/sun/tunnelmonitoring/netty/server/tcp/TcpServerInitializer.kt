import android.os.Environment
import android.util.Log
import com.sun.tunnelmonitoring.MessageEvent
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import org.greenrobot.eventbus.EventBus
import java.io.File

class TcpServerInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(p0: SocketChannel?) {
        val delimiter = "&^%~".toByteArray()
        var pipline = p0!!.pipeline()
        pipline.addLast(DelimiterBasedFrameDecoder(1024 * 1024 * 10, Unpooled.copiedBuffer(delimiter)))
            .addLast("decoder", ByteArrayDecoder())
            .addLast("encoder",ByteArrayEncoder())
            .addLast(TcpServerHandler())

    }

    class TcpServerHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)

            val bytes=msg as ByteArray
            val filepath= Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "rec.txt"
            val file=File(filepath)
            if(!file.exists()) file.createNewFile()
            file.writeBytes(bytes)
            println("接收数据完成")
            EventBus.getDefault().post(MessageEvent("接收到数据，大小为：${bytes.size/1024}KB\n已保存为文件rec.txt\n保存路径：$filepath"))
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