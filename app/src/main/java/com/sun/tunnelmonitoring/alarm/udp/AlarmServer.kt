import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.CharsetUtil
import java.lang.Exception

object AlarmServer {
    private val handler=Handler()
    private const val HOST = "localhost"
    private const val PORT = 3344
    private var bootstrap: Bootstrap?=null
    private var acceptGroup: NioEventLoopGroup?=null
    private var channel: Channel?=null

    fun start(port: Int = PORT) {
        bootstrap= Bootstrap()
        acceptGroup= NioEventLoopGroup()
        bootstrap!!.group(acceptGroup)
            .channel(NioDatagramChannel::class.java as Class<out Channel>?
            )
            .option(ChannelOption.SO_BROADCAST, true)
            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(65535))
            .handler(object:ChannelInitializer<NioDatagramChannel>(){
                override fun initChannel(ch: NioDatagramChannel?) {
                    val pipeline = ch!!.pipeline()
                    pipeline.addLast("handler", AlarmServerHandler())
                }
            })
        Thread {
            try {
                if(channel==null|| !channel!!.isOpen) {
                    channel = bootstrap!!.bind(HOST, port).sync().channel()
                }
                println("UdpServer start success $port")
                handler.post{Toast.makeText(MyApplication.getContext(), "已启动警报服务器", Toast.LENGTH_SHORT).show()}
                Log.i("Alarmserver", "已启动警报服务器")
            } catch (e:Exception){
                Log.i("AlarmTCPServer","启动服务器错误")
            }
        }.start()
    }

    fun getChannel(): Channel {
        return channel!!
    }

    class AlarmServerHandler : SimpleChannelInboundHandler<DatagramPacket>() {
        override fun channelRead0(ctx: ChannelHandlerContext?, packet: DatagramPacket?) {
            Log.i("ChannelRead","接收到数据")
            val buf=packet!!.content()
            val readable=buf.readableBytes()
            val bytes=ByteArray(readable)
            buf.readBytes(bytes)
            Log.i("ChannelRead",bytes.toString(CharsetUtil.UTF_8))
            val sendbuf= Unpooled.copiedBuffer("已接收到数据", CharsetUtil.UTF_8)
            val dp= DatagramPacket(sendbuf,packet.sender())
            ctx!!.writeAndFlush(dp)
        }
    }

}