import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.concurrent.TimeUnit

object TcpServer {
    private val HOST = "localhost"
    private val PORT = 3344
    private lateinit var b: ServerBootstrap
    private lateinit var channel: Channel

    fun start() {
        var bossGroup = NioEventLoopGroup()
        var workGroup = NioEventLoopGroup()
        try {
            b = ServerBootstrap()
            b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(TcpServerInitializer())
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 30)

            println("服务器已启动")
            var future = b.bind(PORT).sync().channel().closeFuture().await().sync()
            if (future.isSuccess) {
                channel = future.channel()
            } else {
                var flag = false
                while (!false) {
                    println("进行重连")
                    channel.disconnect()
                    channel.close()
                    future.channel().eventLoop().schedule({
                        if (future.isSuccess) {
                            channel = future.channel()
                            flag = true
                            println("重连成功")
                        }
                    }, 2, TimeUnit.SECONDS)
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            println("连接客户端失败")
        } finally {
            bossGroup.shutdownGracefully()
            workGroup.shutdownGracefully()
        }
    }
}