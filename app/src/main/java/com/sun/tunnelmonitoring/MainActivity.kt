package com.sun.tunnelmonitoring

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TimeUtils
import android.widget.Toast
import com.sun.tunnelmonitoring.Utils.TimeUtil
import com.sun.tunnelmonitoring.db.manager.Temperature
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import org.litepal.extension.findAll
import pub.devrel.easypermissions.EasyPermissions

private const val PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE = 1

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    title = "主页"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            HomeFragment.get()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_monitor -> {
                    title = "监控中心"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            MonitorFragment.get()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_sysconfig -> {
                    title = "系统配置"
                    supportFragmentManager.inTransaction {
                        replace(
                            R.id.activity_fragment,
                            SysConfigFragment()
                        )
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.inTransaction { add(R.id.activity_fragment, HomeFragment.get()) }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        title = "主页"

        //申请权限
        EasyPermissions.requestPermissions(
            this@MainActivity,
            "没有相应权限软件将无法工作",
            PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        LitePal.deleteAll<Temperature>()
        Temperature(12f,"2018-01-11","12:00").save()
        Temperature(14f,"2018-01-11","12:10").save()
        Temperature(15f,"2018-01-11","12:20").save()
        Temperature( 8f,"2018-01-11","12:30").save()
        Temperature(10f,"2018-01-11","12:40").save()
        Temperature(10f,"2018-01-11","12:50").save()
        Temperature(12f,"2018-01-12","12:00").save()
        Temperature(14f,"2018-01-12","12:10").save()
        Temperature(15f,"2018-01-12","12:20").save()
        Temperature(11f,"2018-01-12","12:30").save()
        Temperature(12f,"2018-01-12","12:40").save()
        Temperature(15f,"2018-01-12","12:50").save()

        val temps=LitePal.where("date like ? and time between ? and ?","2018-01-11","12:00","12:20").find<Temperature>()
        for(temp in temps){
            Log.i("LitePal Qurey",temp.toString())
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    //权限申请回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size > 0) {
                println(grantResults.size)
                for (res in grantResults) {
                    if (res == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "获取到权限", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this, "获取权限被拒绝，重启软件后可重新获取", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
