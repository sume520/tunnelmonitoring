package com.sun.tunnelmonitoring.projectTree

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sun.tunnelmonitoring.R

class ProjectTreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tree)
        title = "基础信息"
        supportFragmentManager
            .beginTransaction().replace(R.id.fg_tree, TreeFragment.get())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
