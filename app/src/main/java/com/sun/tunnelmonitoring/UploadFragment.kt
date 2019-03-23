package com.sun.tunnelmonitoring


import AlarmClient
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_upload.*

class UploadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bt_pulldata.setOnClickListener {
            AlarmClient.sendMessage("get data")
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance:UploadFragment?=null
            get() {
                if(field==null){
                    field= UploadFragment()
                }
                return field
            }
        @Synchronized
        fun get():UploadFragment{
            return instance!!
        }
    }

}
