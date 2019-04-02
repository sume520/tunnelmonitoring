package com.sun.tunnelmonitoring.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sun.tunnelmonitoring.R;

import java.io.IOException;
import java.util.UUID;

public class SettingFragment extends Fragment {
    //文字
    private String deviceAddress;
    private BluetoothDevice bluetoothDevice;
    private TextView tvConnectResult;
    public static BluetoothSocket bluetoothSocket;
    private ConnectThread connectThread;
    private BluetoothAdapter mBluetoothAdapter;
    //Handler对象
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //获得线程数
            int what = msg.what;
            switch (what) {
                case 0://连接失败
                    String defaultContent = (String) msg.obj;
                    tvConnectResult.setText(defaultContent);
                    break;
                case 1://连接成功
                    String successContent = (String) msg.obj;
                    tvConnectResult.setText(successContent);
                    break;
                case 2://连接成功
                    String disContent = (String) msg.obj;
                    tvConnectResult.setText(disContent);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //显示设备名称及地址
        showBleDeviceInfo();

        //获取蓝牙设备
        initConnectBtDevice();

        //连接or断开蓝牙点击事件
        bluetoothClick();
    }

    /**
     * 显示设备名称及地址
     */
    private void showBleDeviceInfo() {
        //获得Intent
        Intent intent = getActivity().getIntent();
        deviceAddress = intent.getStringExtra(Constant.MAIN2PRINTHOME_DEVICE_ADDRESS);
        String deviceName = intent.getStringExtra(Constant.MAIN2PRINTHOME_DEVICE_NAME);

        //蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //获得控件
        TextView tvDeviceName = (TextView) getActivity().findViewById(R.id.printhome_tv_device_name);
        TextView tvDeviceAddress = (TextView) getActivity().findViewById(R.id.printhome_tv_device_address);

        //设置值
        if (deviceName != null && deviceName.length() > 0) {
            tvDeviceName.setText(deviceName);
        } else {
            tvDeviceName.setText("未知设备");
        }

        tvDeviceAddress.setText(deviceAddress);
    }

    /**
     * 获取蓝牙设备
     */
    private void initConnectBtDevice() {
        //设备
        bluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        //连接结果
        tvConnectResult = (TextView) getActivity().findViewById(R.id.printhome_tv_connect_state);

        //开启线程连接
        connectThread = new ConnectThread();
        connectThread.start();
    }


    /**
     * 连接or断开蓝牙点击事件
     */
    private void bluetoothClick() {
        //连接蓝牙
        Button btnConnect = (Button) getActivity().findViewById(R.id.btn_set_connect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "正在连接蓝牙设备,请稍后...", Toast.LENGTH_SHORT).show();
                //开启线程连接
                connectThread = new ConnectThread();
                connectThread.start();
            }
        });

        //断开蓝牙
        Button btnDisConnect = (Button) getActivity().findViewById(R.id.btn_set_disconnect);
        btnDisConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "正在断开连接中...", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        if (connectThread != null) {
                            connectThread.interrupt();
                            connectThread = null;
                        }

                        if (bluetoothSocket != null) {
                            try {
                                bluetoothSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bluetoothSocket = null;
                        }

                        Message msg = mHandler.obtainMessage();
                        msg.what = 2;
                        msg.obj = "已断开连接！";
                        mHandler.sendMessage(msg);
                    }
                }.start();
            }
        });

    }

    /**
     * 连接蓝牙设备线程
     */
    private class ConnectThread extends Thread {
        @Override
        public void run() {
            try {
                //确保已关闭
                mBluetoothAdapter.cancelDiscovery();
                //获得BluetoothSocket对象，需要有效的uuid
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                //连接
                bluetoothSocket.connect();
                //执行完连接操作后，再更改文字内容
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = "连接成功！";
//                tvConnectResult.setText("连接成功！");
                mHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                //连接失败
                tvConnectResult.setText("连接失败,请稍后再次连接！");
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = "连接失败,请稍后再次连接！";
                mHandler.sendMessage(msg);
            }
        }
    }
}
