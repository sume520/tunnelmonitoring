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

    fun write(filename:String) {//发送内存卡根目录指定文件
        try {
            val filename= Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename
            val file = File(filename)
            if(!file.exists()){
                Toast.makeText(MyApplication.getContext(),"文件“$filename”不存在",Toast.LENGTH_SHORT).show()
            }
            val fileInfo = FileInfo(file.name, file.length(), file.readBytes())
            val bytes = ByteObjectConverter.objectToByte(fileInfo)
            val size = bytes.size / 1024
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

    fun start() {//启动客户端
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close(){
        workerGroup.shutdownGracefully()
        Toast.makeText(MyApplication.getContext(),"已关闭客户端",Toast.LENGTH_SHORT).show()
    }

}