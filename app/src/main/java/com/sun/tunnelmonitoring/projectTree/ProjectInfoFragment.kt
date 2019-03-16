package com.sun.tunnelmonitoring.projectTree


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.Utils.SENSORS
import kotlinx.android.synthetic.main.fragment_home.*

class ProjectInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_info, container, false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("onNothingSelected", "nothing selected")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.i("onItemSelected", "position: $position, id: $id, sensor: ${SENSORS[position]}")
        // selectSersor(SENSORS[position])
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //下拉列表适配器
        val adapter = ArrayAdapter.createFromResource(
            activity,
            R.array.sensor_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_sensor.adapter = adapter
        spin_sensor.onItemSelectedListener = this

    }
}
