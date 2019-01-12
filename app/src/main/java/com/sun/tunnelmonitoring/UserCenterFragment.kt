package com.sun.tunnelmonitoring


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.tunnelmonitoring.db.manager.UserDao
import com.sun.tunnelmonitoring.login.LoginActivity
import com.sun.tunnelmonitoring.login.LoginFragment
import kotlinx.android.synthetic.main.fragment_user_center.*

class UserCenterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_center, container, false)
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance:UserCenterFragment?=null
            get() {
                if(field==null){
                    field= UserCenterFragment()
                }
                return field
            }
        @Synchronized
        fun get():UserCenterFragment{
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
