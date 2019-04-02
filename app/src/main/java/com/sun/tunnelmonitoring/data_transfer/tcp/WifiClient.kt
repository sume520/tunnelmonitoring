import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication
import com.sun.tunnelmonitoring.Utils.getAPIP
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress


object WifiClient {
    private var HOST = "localhost"
    private val PORT = 3344
    private lateinit var group: EventLoopGroup
    private lateinit var b: Bootstrap
    private var channel: Channel? = null
    private val handler = Handler()

    fun start(): Int {
        var flag: Int
        if (isConnectToAP())
            HOST = getAPIP()
        else {
            return 2
        }
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
            Log.e("WifiClient", "连接服务器失败")
            //handler.post { Toast.makeText(MyApplication.getContext(), "连接到服务器", Toast.LENGTH_SHORT).show() }
            flag = 0
        } catch (e: Exception) {
            Log.e("WifiClient", "连接服务器失败")
            flag = 1
        } finally {
            //group.shutdownGracefully()
            //println("客户端已关闭")
        }
        return flag
    }

    private fun isConnectToAP(): Boolean {
        val connManager = MyApplication.getContext()
                .applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val mWifiInfo = connManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifiInfo.isConnected

    }

    fun sendMessage(msg: String) {//发送消息
        //若服务器没有启动，先启动服务器
        if (channel == null) {
            val flag=this.start()
            if (flag == 2) {
                handler.post {
                    Toast.makeText(
                            MyApplication.getContext(),
                            "请先连接到节点网络",
                            Toast.LENGTH_SHORT)
                            .show()
                }
                return
            }
            else if(flag==1){
                handler.post {
                    Toast.makeText(
                            MyApplication.getContext(),
                            "获取数据失败，没有连接到节点",
                            Toast.LENGTH_SHORT)
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
                            "获取数据失败，连接错误",
                            Toast.LENGTH_SHORT)
                            .show()
                }
                return
            }
        }
        if (channel!!.writeAndFlush(msg.toByte()).isSuccess) {
            handler.post {
                Toast.makeText(
                        MyApplication.getContext(),
                        "获取数据成功",
                        Toast.LENGTH_SHORT)
                        .show()
            }
        }

    }
}