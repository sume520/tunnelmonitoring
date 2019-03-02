package com.sun.tunnelmonitoring


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.tunnelmonitoring.Utils.SharedPreferencesUtils
import com.sun.tunnelmonitoring.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_sys_config.*

class SysConfigFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sys_config, container, false)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance:SysConfigFragment?=null
            get() {
                if(field==null){
                    field= SysConfigFragment()
                }
                return field
            }
        @Synchronized
        fun get():SysConfigFragment{
            return instance!!
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bt_logout.setOnClickListener {
            SharedPreferencesUtils.setLoginStatus(false,MyApplication.getContext())
            val intent =Intent(activity,LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }

}
