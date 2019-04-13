package com.sun.tunnelmonitoring.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.sun.tunnelmonitoring.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class TextFragment extends Fragment {
    private EditText etContent;
    private TextView rqContent;
    byte[] buffer=new byte[1024];
    int bytes;
    private String ss=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //返回显示视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text, container, false);
    }
    //执行逻辑代码
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       etContent=(EditText) getActivity().findViewById(R.id.fg_et_content);
       Button btnSend=getActivity().findViewById(R.id.fg_btn_send);
       rqContent=(TextView)getActivity().findViewById(R.id.fg_tv_content);
        btnSend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            String content=etContent.getText().toString();
            if (SettingFragment.bluetoothSocket==null){
                Toast.makeText(getActivity(),"本设备不支持蓝牙操作",Toast.LENGTH_SHORT).show();
                return;
            }else {
                Toast.makeText(getActivity(),"正在发送中",Toast.LENGTH_SHORT).show();
            }
               try {
                 OutputStream outputStream=SettingFragment.bluetoothSocket.getOutputStream();
                 PrintUtil.setOutputStream(outputStream);
                 //发送文字
                 PrintUtil.printText(content);
                 new Thread(new Runnable() {
                     @Override
                     public void run() {
                         while (true) {
                             InputStream inputStream = null;
                             try {
                                 inputStream = SettingFragment.bluetoothSocket.getInputStream();
                                 bytes = inputStream.read(buffer);
                                 ss = new String(buffer, 0, bytes, "utf-8");
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                             Log.i("1111", ss);
                             rqContent.setText(ss);
                         }
                     }
                 }).start();
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
       });



    }


}
