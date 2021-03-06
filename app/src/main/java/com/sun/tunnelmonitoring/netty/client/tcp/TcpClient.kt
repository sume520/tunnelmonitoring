import android.os.Environment
import android.os.Handler
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.io.File
import java.net.InetSocketAddress

object TcpClient {
    private val HOST = "localhost"
    private val PORT = 3344
    private lateinit var group: EventLoopGroup
    private lateinit var b: Bootstrap
    private var channel: Channel? = null
    private val handler = Handler()

    fun start() {
        group = NioEventLoopGroup()
        try {
            b = Bootstrap()
            b.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024 * 30)
                .handler(WifiClientInitializer())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000 * 10)//设置连接超时时间
                .option(ChannelOption.SO_KEEPALIVE, true)

            channel = b.connect(HOST, PORT).sync().channel()
            println("==============与服务器连接")
            handler.post { Toast.makeText(MyApplication.getContext(), "连接到服务器", Toast.LENGTH_SHORT).show() }
        } catch (e: Exception) {
            println("==============连接服务器失败")
        } finally {
            // group.shutdownGracefully()
            //println("客户端已关闭")
        }
    }

    fun sendMessage(msg: String) {//发送消息
        //若服务器没有启动，先启动服务器
        if (channel == null)
            this.start()
        else if (!channel!!.isActive)//若channel未连接，调用connect函数连接
            channel!!.connect(InetSocketAddress(HOST, PORT))
        channel!!.writeAndFlush(msg.toByte())
    }

    fun sendFile(filename: String) {
        val filepath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename
        val file = File(filepath)
        if (!file.exists()) {
            Toast.makeText(MyApplication.getContext(), "文件“$filename”不存在", Toast.LENGTH_SHORT).show()
        }

        Thread {
            try {
                val size = file.length() / 1024
                val delimiter = "&^%~".toByteArray()//结束符
                println("待发送数据大小：${size}Kb")
                val bytes = file.readBytes()
                channel!!.writeAndFlush(bytes).sync()
                channel!!.writeAndFlush(delimiter)//最后发送结束符结束粘包
                println("发送完成")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                println("发送失败")
            }
        }.start()
    }

    fun closeClient() {
        channel!!.disconnect().sync()
        channel!!.close().sync()
        group.shutdownGracefully()
    }
}