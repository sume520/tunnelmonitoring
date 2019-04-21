import android.os.Environment
import android.util.Log
import com.sun.tunnelmonitoring.events.AlarmEvent
import com.sun.tunnelmonitoring.events.MessageEvent
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
import io.netty.util.CharsetUtil
import org.greenrobot.eventbus.EventBus
import java.io.File

class AlarmTCPServerInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(p0: SocketChannel?) {
        val delimiter = "&^%~".toByteArray()
        val pipline = p0!!.pipeline()
        pipline.addLast(DelimiterBasedFrameDecoder(1024 * 1024 * 10, Unpooled.copiedBuffer(delimiter)))
            .addLast("decoder", ByteArrayDecoder())
            .addLast("encoder",ByteArrayEncoder())
            .addLast(TcpServerHandler())

    }

    class TcpServerHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)

            val bytes=msg as ByteArray
            val message=bytes.toString(CharsetUtil.UTF_8)
            println(">>>>>>>>>>>>>>>>>>")
            Log.i("AlarmServer",message)
            EventBus.getDefault().post(AlarmEvent("节点异常警报",message))
        }
    }

}