import android.os.Handler
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import java.lang.Exception

object UDPServer {
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
            .handler(UdpServerInitializer.UdpServerHandler())
        Thread {
            try {
                if(channel==null|| !channel!!.isOpen) {
                    channel = bootstrap!!.bind(host, port).sync().channel()
                }
                println("UdpServer start success $port")
                handler.post{Toast.makeText(MyApplication.getContext(), "已启动udp服务器", Toast.LENGTH_SHORT).show()}
                Log.d("udpserver", "已启动udp服务器")
                //channel!!.closeFuture().await()
            } finally {
                //acceptGroup.shutdownGracefully()
                //println("UDP服务器已关闭")
            }
        }.start()
    }

    fun getChannel(): Channel {
        return channel!!
    }

    fun close(){
        if(acceptGroup!=null)
            acceptGroup!!.shutdownGracefully().sync()
        Toast.makeText(MyApplication.getContext(),"已关闭UDP服务器",Toast.LENGTH_SHORT).show()
    }

}