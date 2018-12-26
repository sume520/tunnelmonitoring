import android.util.Log
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import java.net.InetSocketAddress

object UdpUtil {
    private lateinit var bootstrap: Bootstrap
    private lateinit var workerGroup: NioEventLoopGroup
    private var channel: Channel? = null

    init {
        if (channel == null)
            this.start()
    }

    fun getChannel(): Channel? {
        return channel
    }

    fun write(msg: String) {
        var hexString = msg.Hex2Byte()
        Thread {
            try {
                channel!!.writeAndFlush(
                    DatagramPacket(
                        Unpooled.copiedBuffer(hexString),
                        InetSocketAddress("47.107.158.26", 7788)
                    )
                ).sync()
                Log.d("UdpUtil", "发送成功")
                //Toast.makeText(MyApplication.getContext(),"发送成功",Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UdpUtil", "发送出错")
            }
        }.start()
    }

    fun start() {
        try {
            bootstrap = Bootstrap()
            workerGroup = NioEventLoopGroup()
            bootstrap.group(workerGroup)
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(UdpClientHandler())
            channel = bootstrap.bind(0).sync().channel()
            //Toast.makeText(MyApplication.getContext(),"已启动UDP客户端",Toast.LENGTH_SHORT).show()
            Log.d("ClientUtil", "已启动UDP客户端")
            //channel.closeFuture().await(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //workerGroup.shutdownGracefully()
            Log.d("UdpUtil", "已关闭与服务器连接")
        }
    }

    fun String.Hex2Byte(): ByteArray? {
        var hexString = this
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0..length - 1) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    private fun charToByte(c: Char): Byte {

        return "0123456789ABCDEF".indexOf(c).toByte()
    }
}