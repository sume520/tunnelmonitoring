import android.os.Environment
import com.sun.tunnelmonitoring.File.saveToFile
import com.sun.tunnelmonitoring.events.MessageEvent
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import org.greenrobot.eventbus.EventBus
import java.io.File

class TcpClientInitializer : ChannelInitializer<SocketChannel>() {

    override fun initChannel(channel: SocketChannel?) {
        val delimiter = "&^%~".toByteArray()//粘包分隔符
        val pipeline = channel!!.pipeline()
        pipeline.addLast(DelimiterBasedFrameDecoder(1024 * 1024 * 10, Unpooled.copiedBuffer(delimiter)))
            .addLast("encoder", ByteArrayEncoder())
            .addLast("decoder",ByteArrayDecoder())
            .addLast("handler", TcpClientHandler())
    }

    inner class TcpClientHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)
            val rec = msg as String
            println("接收到数据：$rec")
            val bytes=msg as ByteArray
            saveToFile(bytes, "rec")
            EventBus.getDefault().post(MessageEvent("已保存文件“rec.txt”到SD卡根目录"))
            //接收完成后关闭客户端
            ctx!!.channel().close()
            ctx.close().sync()
        }
    }

}
