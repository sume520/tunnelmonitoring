package com.sun.tunnelmonitoring.utils

import android.widget.Toast
import com.sun.tunnelmonitoring.MyApplication

fun toast(msg: String) {
    Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show()
}