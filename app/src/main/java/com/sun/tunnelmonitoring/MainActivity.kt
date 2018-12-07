package com.sun.tunnelmonitoring

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

private const val PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE=1

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.inTransaction { replace(R.id.activity_fragment, HomeFragment.newInstance()) }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.inTransaction { add(R.id.activity_fragment, HomeFragment.newInstance()) }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //getPermissions()
        EasyPermissions.requestPermissions(
            this@MainActivity,
            "没有相应权限软件将无法工作",
            PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    private fun getPermissions() {
        var permissionList = arrayOfNulls<String?>(2)

        //若没有权限，先获取权限
        if (ActivityCompat.checkSelfPermission(
                this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.set(0, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ActivityCompat.checkSelfPermission(
                this
                , Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.set(1, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!permissionList.isEmpty()) {
            requestPermissions(permissionList, PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE)
        } else {
            Log.d("requestpermissons", "已获取所有权限")
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    inner class SocketReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var text = intent!!.getStringExtra("rec_text")
            tv_rectext.text = text
        }
    }

    //权限申请回调
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode== PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE){
            if(grantResults.size>0){
                println(grantResults.size)
                for(res in grantResults){
                    if(res==PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this,"获取到权限", Toast.LENGTH_SHORT).show()
                    else{
                        Toast.makeText(this,"获取权限被拒绝，重启软件后将重新获取", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
