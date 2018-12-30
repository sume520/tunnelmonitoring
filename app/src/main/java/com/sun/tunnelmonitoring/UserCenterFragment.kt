package com.sun.tunnelmonitoring


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        @JvmStatic
        fun newInstance() =
            UserCenterFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tx_user_center.setOnClickListener {
            val intent= Intent(activity,LoginActivity::class.java)
            intent.putExtra("param","login")
            startActivity(intent)
        }


    }

}
