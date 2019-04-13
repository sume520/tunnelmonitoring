package com.sun.tunnelmonitoring.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

public class PrintUtil {

    private static OutputStream outputStream = null;

    //设置IO流
    public static void setOutputStream(OutputStream outputStream) {
        PrintUtil.outputStream = outputStream;
    }


    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public static void printText(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            //Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
