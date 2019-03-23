import com.sun.tunnelmonitoring.events.AlarmEvent
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import org.greenrobot.eventbus.EventBus

class AlarmClientInitializer : ChannelInitializer<SocketChannel>() {

    override fun initChannel(channel: SocketChannel?) {
        val delimiter = "&^%~".toByteArray()//粘包分隔符
        val pipeline = channel!!.pipeline()
        pipeline.addLast(DelimiterBasedFrameDecoder(1024 * 1024 * 10, Unpooled.copiedBuffer(delimiter)))
            .addLast("encoder", ByteArrayEncoder())
            .addLast("decoder", ByteArrayDecoder())
            .addLast("handler", TcpClientHandler())
    }

    inner class TcpClientHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)
            EventBus.getDefault().post(AlarmEvent(msg.toString()))
        }
    }
}
