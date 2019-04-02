import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel

object AlarmServer {
    private val handler=Handler()
    private const val HOST = "localhost"
    private const val PORT = 3344
    private var bootstrap: Bootstrap?=null
    private var acceptGroup: NioEventLoopGroup?=null
    private var channel: Channel?=null

    fun start(host: String = HOST, port: Int = PORT) {
        bootstrap= Bootstrap()
        acceptGroup= NioEventLoopGroup()
        bootstrap!!.group(acceptGroup)
            .channel(NioDatagramChannel::class.java)
            .option(ChannelOption.SO_BROADCAST, true)
            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(65535))
            .handler(AlarmServerInitializer.AlarmServerHandler())
        Thread {
            try {
                if(channel==null|| !channel!!.isOpen) {
                    channel = bootstrap!!.bind(host, port).sync().channel()
                }
                println("UdpServer start success $port")
                handler.post{Toast.makeText(MyApplication.getContext(), "已启动udp服务器", Toast.LENGTH_SHORT).show()}
                Log.d("udpserver", "已启动udp服务器")
            } catch (e:Exception){
                Log.i("AlarmServer","启动服务器错误")
            }
        }.start()
    }

    fun getChannel(): Channel {
        return channel!!
    }

}