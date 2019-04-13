package com.sun.tunnelmonitoring.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sun.tunnelmonitoring.MyApplication;
import com.sun.tunnelmonitoring.R;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final int REQUEST_ENABLE_BT = 1;
    private ListView mDeviceListView;//蓝牙适配器对象
    private BluetoothAdapter mBluetoothAdapter;
    //请求响应码
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BtListAdapter btListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bluetooth_list);
        //初始化蓝牙设备
        initBluetoothAdapter();//获得蓝牙适配器
        mDeviceListView = (ListView) findViewById(R.id.main_lv_listview);

        btListAdapter = new BtListAdapter();
        //设置适配器
        mDeviceListView.setAdapter(btListAdapter);
        mDeviceListView.setOnItemClickListener(this);
        //广播接收器
        IntentFilter intentFilter = new IntentFilter();
        //添加action值()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, intentFilter);

        //Android 6.0及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //初始化蓝牙设备
                    initBluetoothAdapter();

                    //启动
                    mBluetoothAdapter.startDiscovery();

                    //过滤器
                    IntentFilter intentFilter = new IntentFilter();
                    //添加action
                    intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                    intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    //注册广播接收器
                    registerReceiver(mReceiver, intentFilter);

                    //获得ListView控件
                    mDeviceListView = (ListView) findViewById(R.id.main_lv_listview);

                    //适配器(在本类中创建)
                    btListAdapter = new BtListAdapter();

                    //给ListView设置适配器
                    mDeviceListView.setAdapter(btListAdapter);

                    //item项点击事件
//                    mDeviceListView.setOnItemClickListener(this);
                }
                break;
        }
    }

    //广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btListAdapter.addBluetoothDevice(device);
                //刷新数据
                btListAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.cancelDiscovery();//停止扫描
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获得BluetoothDevice对象
        BluetoothDevice bluetoothDevice = btListAdapter.getBluetoothDevice(position);
        String name = bluetoothDevice.getName();
        String address = bluetoothDevice.getAddress();
        Intent intent = new Intent(BluetoothActivity.this, CommunityActivity.class);
        intent.putExtra(Constant.MAIN2PRINTHOME_DEVICE_NAME, name);
        intent.putExtra(Constant.MAIN2PRINTHOME_DEVICE_ADDRESS, address);
        startActivity(intent);
    }


    private class BtListAdapter extends BaseAdapter {
        private final ArrayList<BluetoothDevice> mBtDatas;
        private final LayoutInflater layoutInflater;

        //构造方法
        BtListAdapter() {
            this.mBtDatas = new ArrayList<>();
            //获取布局加载器
            this.layoutInflater = getLayoutInflater();
        }

        /**
         * 添加蓝牙设备
         *
         * @param device
         */
        void addBluetoothDevice(BluetoothDevice device) {
            //判断内部是否是BluetoothDevice类型
            if (!mBtDatas.contains(device)) {
                mBtDatas.add(device);
            }
        }

        /**
         * 清除数据
         */
        void clear() {
            //清空数据
            mBtDatas.clear();
        }

        /**
         * 向外提供获得BluetoothDevice对象的方法
         *
         * @param position
         * @return
         */
        BluetoothDevice getBluetoothDevice(int position) {
            return mBtDatas.get(position);
        }


        //item项的数目
        @Override
        public int getCount() {
            return mBtDatas.size();
        }

        //item的对象,手指点击的位置
        @Override
        public Object getItem(int position) {
            return mBtDatas.get(position);
        }

        //返回id
        @Override
        public long getItemId(int id) {
            return id;
        }

        //返回item项的布局视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            //复用convertView
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.scan_lv_item_layout, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.scan_lv_item_device_name);
                viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.scan_lv_item_device_address);

                //设置tag
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //把名称地址设置到item项
            BluetoothDevice bluetoothDevice = (BluetoothDevice) getItem(position);
            //name
            String name = bluetoothDevice.getName();
            //address
            String address = bluetoothDevice.getAddress();

            if (name != null && name.length() > 0) {
                viewHolder.tvName.setText(name);
            } else {
                viewHolder.tvName.setText("未知设备");
            }

            viewHolder.tvAddress.setText(address);

            return convertView;
        }

        //定义ViewHolder
        class ViewHolder {
            //            private LinearLayout layout;
            //name
            private TextView tvName;
            //address
            private TextView tvAddress;
        }
    }

    /**
     * 初始化蓝牙设备
     */
    private void initBluetoothAdapter() {
        //获得适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //若没有获得蓝牙适配器，返回上一个界面
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "本设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            //关闭当前Activity
            finish();
        }
        //没开启则开启
        if (!mBluetoothAdapter.isEnabled()) {
            try {
                //开启蓝牙
                //mBluetoothAdapter.enable();
                //弹出开启蓝牙设备的对话框，让用户开启蓝牙
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //启动
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MyApplication.Companion.getContext(),"打开蓝牙出错",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        mBluetoothAdapter.startDiscovery();//后台扫描
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter = null;
    }

    //停止扫描
    public void stopScanBtDevice(View view) {
        Toast.makeText(this, "已停止扫描。。。", Toast.LENGTH_SHORT).show();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    //重新扫描
    public void rescanBtDevice(View view) {
        Toast.makeText(this, "正在重新扫描蓝牙设备", Toast.LENGTH_SHORT).show();
        btListAdapter.clear();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
        }
    }
}
