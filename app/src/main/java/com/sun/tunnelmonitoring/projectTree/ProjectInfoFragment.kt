package com.sun.tunnelmonitoring.projectTree


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.squareup.okhttp.Request
import com.sun.tunnelmonitoring.R
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.fragment_home.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProjectInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var dataType: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataType = resources.getStringArray(R.array.data_types)
        return inflater.inflate(R.layout.fragment_project_info, container, false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Log.i("onNothingSelected", "nothing selected")
        parent!!.setSelection(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type = dataType[position]
        Log.i("onItemSelected", "position: $position, id: $id, data type: $type")
        OkHttpUtils
            .post()
            .url("http://47.107.158.26:80/app/tree/data")
            .addParams("number", "a0001")
            .addParams("type", type)
            .build()
            .execute(DataCallback())
        // selectSersor(SENSORS[position])
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //下拉列表适配器
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.data_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_sensor.adapter = adapter
        spin_sensor.onItemSelectedListener = this
    }

    class DataCallback : StringCallback() {
        override fun onResponse(response: String?) {
            Log.i("DataCallback", response)
        }

        override fun onError(request: Request?, e: Exception?) {
            Log.e("DataCallback", e!!.message)
        }
    }
}
