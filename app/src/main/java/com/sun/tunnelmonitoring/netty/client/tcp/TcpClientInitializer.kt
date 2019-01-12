import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder

class TcpClientInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(p0: SocketChannel?) {
        var pipeline = p0!!.pipeline()
        pipeline.addLast("encoder",ByteArrayEncoder())
            .addLast("decoder",ByteArrayDecoder())
            .addLast("handler", TcpClientHandler())

    }

    class TcpClientHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)
            var rec = msg as String
            println("服务器消息：$rec")
        }
    }

}
