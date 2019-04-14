import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress

object AlarmClient {
    private val HOST = "192.168.1.143"
    private val PORT = 3344
    private lateinit var group: EventLoopGroup
    private lateinit var b: Bootstrap
    private var channel: Channel? = null
    private val handler = Handler()

    fun start(): Boolean {
        var flag: Boolean
        group = NioEventLoopGroup()
        try {
            b = Bootstrap()
            b.group(group).channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024 * 30)
                .handler(AlarmClientInitializer())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000 * 10)//设置连接超时时间
                .option(ChannelOption.SO_KEEPALIVE, true)

            channel = b.connect(HOST, PORT).sync().channel()
            Log.e("WifiClient", "连接服务器失败")
            //handler.post { Toast.makeText(MyApplication.getContext(), "连接到服务器", Toast.LENGTH_SHORT).show() }
            flag = true
        } catch (e: Exception) {
            Log.e("WifiClient", "连接服务器失败")
            flag = false
        }
        return flag
    }

    fun sendMessage(msg: String) {//发送消息
        //若服务器没有启动，先启动服务器
        if (channel == null) {
            if (!this.start()) {
                handler.post {
                    Toast.makeText(
                        MyApplication.getContext(),
                        "发送失败，网络未连接",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                return
            }
        } else if (!channel!!.isActive) {//若channel未连接，调用connect函数连接
            val future = channel!!.connect(InetSocketAddress(HOST, PORT))
            if (!future.isSuccess) {
                handler.post {
                    Toast.makeText(
                        MyApplication.getContext(),
                        "发送失败，网络连接失败",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                return
            }
        }
        if (channel!!.writeAndFlush(msg.toByte()).isSuccess) {
            handler.post {
                Toast.makeText(
                    MyApplication.getContext(),
                    "发送成功",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    }
}