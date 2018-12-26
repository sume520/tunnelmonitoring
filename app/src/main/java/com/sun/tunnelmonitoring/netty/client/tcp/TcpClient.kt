import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.io.File

object TcpClient {
    private val HOST = "localhost"
    private val PORT = 3344
    private lateinit var group: EventLoopGroup
    private lateinit var b: Bootstrap
    private var channel: Channel? = null

    fun start() {
        group = NioEventLoopGroup()
        try {
            b = Bootstrap()
            b.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024 * 30)
                .handler(TcpClientInitializer())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000 * 10)//设置连接超时时间
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
            channel = b.connect(HOST, PORT).sync().channel()
            println("与服务器连接")
            //channel.closeFuture().await()
        } catch (e: Exception) {
            println("连接服务器失败")
        } finally {
            // group.shutdownGracefully()
            //println("客户端已关闭")
        }
    }

    fun sendFile() {
        try {
            var file = File("D:/2.jpg")
            var fileInfo = FileInfo(file.name, file.length(), file.readBytes())
            channel!!.writeAndFlush(fileInfo).sync()
            println("发送完成")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            println("发送失败")
        }
    }


}