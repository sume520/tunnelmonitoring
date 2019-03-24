package com.sun.tunnelmonitoring.projectTree

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.transition.TransitionInflater
import android.view.View
import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.events.RefreshEvent
import kotlinx.android.synthetic.main.activity_project_tree.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ProjectTreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setupWindowAnimations()
        setContentView(R.layout.activity_project_tree)
        title = "基础信息"
        supportFragmentManager
            .beginTransaction().replace(com.sun.tunnelmonitoring.R.id.fg_tree, TreeFragment.get())
            .commit()

        EventBus.getDefault().register(this)
    }

    private fun setupWindowAnimations() {
        val slideTracition = TransitionInflater.from(this).inflateTransition(R.transition.slide_from_right)
        window.enterTransition = slideTracition
        window.exitTransition = slideTracition
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isRefresh(refreshEvent: RefreshEvent) {
        if (refreshEvent.flag)
            pb_refresh.visibility = View.VISIBLE
        else
            pb_refresh.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
