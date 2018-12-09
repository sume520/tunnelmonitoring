import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.util.CharsetUtil

class UdpClientHandler : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, packet: DatagramPacket?) {
        var body = packet!!.content().toString(CharsetUtil.UTF_8)
        println("来自服务器的消息：${body}")
    }
}