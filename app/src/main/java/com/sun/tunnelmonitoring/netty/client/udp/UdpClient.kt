import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import java.io.File
import java.net.InetSocketAddress

object UdpClient {
    private var handler=Handler()
    private lateinit var bootstrap: Bootstrap
    private lateinit var workerGroup: NioEventLoopGroup
    private lateinit var channel: Channel

    fun getChannel(): Channel {
        return channel
    }

    fun write(filename:String) {
        try {
            var filename= Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename
            var file = File(filename)
            var fileInfo = FileInfo(file.name, file.length(), file.readBytes())
            var bytes = ByteObjectConverter.objectToByte(fileInfo)
            var size = bytes.size / 1024
            println("待发送数据大小：${size}Kb")
            Thread {
                try {
                    channel.writeAndFlush(
                        DatagramPacket(
                            Unpooled.copiedBuffer(bytes),
                            InetSocketAddress("localhost", 3344)
                        )
                    ).sync()
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("发送出错")
                }

            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun start() {
        try {
            bootstrap = Bootstrap()
            workerGroup = NioEventLoopGroup()
            bootstrap.group(workerGroup)
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_SNDBUF,1024*1024)
                .handler(UdpClientHandler())

            channel = bootstrap.bind(0).sync().channel()
            Toast.makeText(MyApplication.getContext(),"已启动UDP客户端",Toast.LENGTH_SHORT).show()
            Log.d("ClientUtil", "已启动UDP客户端")
            //channel.closeFuture().await(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //workerGroup.shutdownGracefully()
            //println("客户端已关闭")
        }

    }

}