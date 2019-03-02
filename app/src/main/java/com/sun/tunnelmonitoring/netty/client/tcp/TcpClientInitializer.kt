import android.os.Environment
import com.sun.tunnelmonitoring.MessageEvent
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import org.greenrobot.eventbus.EventBus
import java.io.File

class TcpClientInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(p0: SocketChannel?) {
        var pipeline = p0!!.pipeline()
        pipeline.addLast("encoder",ByteArrayEncoder())
            .addLast("decoder",ByteArrayDecoder())
            .addLast("handler", TcpClientHandler())

    }

    class TcpClientHandler : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            super.channelRead(ctx, msg)
            val rec = msg as String
            println("接收到数据：$rec")

            val bytes=msg as ByteArray
            //保存到sd卡根目录rec.txt文件中
            val filepath= Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "rec.txt"
            val file= File(filepath)
            //检测文件是否存在
            if(!file.exists()) file.createNewFile()
            file.writeBytes(bytes)
            println("接收数据完成")
            EventBus.getDefault().post(MessageEvent("接收到数据，大小为：${bytes.size/1024}KB\n已保存为文件rec.txt\n保存路径：$filepath"))

            //接收完成后关闭客户端
            ctx!!.close().sync()
        }
    }

}
