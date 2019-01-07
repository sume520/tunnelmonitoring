import android.os.Handler
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
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
    private var channel: Channel? = null
    private var bossGroup: NioEventLoopGroup? = null
    private var workGroup: NioEventLoopGroup? = null
    private var flag = false
    private val hander = Handler()

    fun start() {
        bossGroup = NioEventLoopGroup()
        workGroup = NioEventLoopGroup()
        Thread {
            try {
                b = ServerBootstrap()
                b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(TcpServerInitializer())
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 30)

                println("服务器已启动")
                hander.post {
                    Toast.makeText(
                        MyApplication.getContext(),
                        "启动服务器成功",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val future = b.bind(PORT).sync().channel().closeFuture().await().sync()
                if (future.isSuccess) {
                    channel = future.channel()
                } else {
                    while (!flag) {
                        println("进行重连")
                        channel!!.disconnect()
                        channel!!.close()
                        future.channel().eventLoop().schedule({
                            if (future.isSuccess) {
                                channel = future.channel()
                                flag = true
                                println("重连成功")
                            }
                        }, 2, TimeUnit.SECONDS)
                    }
                    flag = false
                }
            } catch (e: Exception) {
                println("连接客户端失败")
            } finally {
                bossGroup!!.shutdownGracefully()
                workGroup!!.shutdownGracefully()
            }
        }.start()
    }

    fun close() {
        try {
            bossGroup!!.shutdownGracefully().sync()
            workGroup!!.shutdownGracefully().sync()
            Toast.makeText(MyApplication.getContext(), "关闭服务器成功", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(MyApplication.getContext(), "关闭服务器失败", Toast.LENGTH_SHORT).show()
        }
    }
}