package com.sun.tunnelmonitoring.login


import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.okhttp.*

import com.sun.tunnelmonitoring.R
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder

class RegisterFragment : Fragment() {
    private var jsonObject: JSONObject? = null
    private var jsonString: String? = null
    private val URL = "http://192.168.31.175:1234/user/applogin"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val handler = Handler(Handler.Callback { msg ->
            var message = msg.obj as String
            try {
                message = URLDecoder.decode(message, "utf-8")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            false
        })

        reg_et_register.setOnClickListener {
            Thread{
                val name = reg_et_account.text.toString()
                val password = reg_et_passwd.text.toString()
                val danwei = reg_et_department.text.toString()
                val zhiwei = reg_et_position.text.toString()
                val yx = reg_et_email.text.toString()
                val pn = reg_et_phonenum.text.toString()
                val bumen = reg_et_sector.text.toString()
                val client = OkHttpClient()
                jsonObject = null
                jsonObject = JSONObject()
                try {
                    jsonObject!!.put("username", name)
                    jsonObject!!.put("phone", pn)
                    jsonObject!!.put("password", password)
                    jsonObject!!.put("bumen", bumen)
                    jsonObject!!.put("youxiang", yx)
                    jsonObject!!.put("danwei", danwei)
                    jsonObject!!.put("zhiwei", zhiwei)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                jsonString = null
                jsonString = jsonObject.toString()
                val body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonString)
                val request = Request.Builder()
                        .url("http://192.168.43.129:1234/user/applogin")
                        .post(body)
                        .build()
                val call = client.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(request: Request, e: IOException) {
                        e.printStackTrace()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(response: Response) {
                        val msg = handler.obtainMessage()
                        msg.obj = response.body().string()
                        handler.sendMessage(msg)
                    }
                })
            }.start()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
                RegisterFragment()
    }
}
