import android.os.Environment
import com.sun.tunnelmonitoring.events.MessageEvent
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.CharsetUtil
import org.greenrobot.eventbus.EventBus
import java.io.File

class UdpServerInitializer : ChannelInitializer<NioDatagramChannel>() {

    override fun initChannel(ch: NioDatagramChannel?) {
        val pipeline = ch!!.pipeline()
        pipeline.addLast("handler", UdpServerHandler())
    }

    class UdpServerHandler : SimpleChannelInboundHandler<DatagramPacket>() {
        override fun channelRead0(ctx: ChannelHandlerContext?, packet: DatagramPacket?) {
            val buf = packet!!.content()
            val readable = buf.readableBytes()
            val bytes = ByteArray(readable)
            buf.readBytes(bytes)
            println("接收到的数据大小：${bytes.size / 1024}Kb")
            val fileInfo = ByteObjectConverter.byteToObject(bytes) as FileInfo
            val filetype = fileInfo.filename.split('.')[1]
            val filepath= Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "rec.${filetype}"
            val file = File(filepath)
            if (!file.exists()) file.createNewFile()
            file.writeBytes(fileInfo.filedata)
            println("接收数据完成")
            val size = bytes.size / 1024
            println("文件大小：${size}Kb")
            EventBus.getDefault().post(MessageEvent(fileInfo.toString()))

            val sendbuf=Unpooled.copiedBuffer("已接收到文件：${fileInfo.filename}",CharsetUtil.UTF_8)
            val dp=DatagramPacket(sendbuf,packet.sender())
            ctx!!.writeAndFlush(dp)
        }
    }
}