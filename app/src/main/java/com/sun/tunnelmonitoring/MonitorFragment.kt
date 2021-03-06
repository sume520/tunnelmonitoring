package com.sun.tunnelmonitoring


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.tunnelmonitoring.alarm.AlarmActivity
import kotlinx.android.synthetic.main.fragment_monitor.*


class MonitorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monitor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bt_live_measure.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction().add(R.id.activity_fragment, LiveMeasureFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        bt_data_warnning.setOnClickListener {
            val intent = Intent(activity, AlarmActivity::class.java)
            startActivity(intent)
        }

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MonitorFragment? = null
            get() {
                if (field == null) {
                    field = MonitorFragment()
                }
                return field
            }

        @Synchronized
        fun get(): MonitorFragment {
            return instance!!
        }
    }
}