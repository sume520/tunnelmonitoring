import android.util.Log
import com.sun.tunnelmonitoring.events.AlarmEvent
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import org.greenrobot.eventbus.EventBus

class AlarmServerInitializer : ChannelInitializer<NioDatagramChannel>() {

    override fun initChannel(ch: NioDatagramChannel?) {
        val pipeline = ch!!.pipeline()
        pipeline.addLast("handler", AlarmServerHandler())
    }
    @ChannelHandler.Sharable
    class AlarmServerHandler : SimpleChannelInboundHandler<DatagramPacket>() {
        override fun channelRead0(ctx: ChannelHandlerContext?, packet: DatagramPacket?) {
            EventBus.getDefault().post(AlarmEvent(packet!!.content().toString()))
            Log.i("channelRead", packet.content().toString())
        }
    }
}