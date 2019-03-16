package com.sun.tunnelmonitoring.projectTree

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.transition.TransitionInflater
import com.sun.tunnelmonitoring.R


class ProjectTreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setupWindowAnimations()
        setContentView(R.layout.activity_project_tree)
        title = "基础信息"
        supportFragmentManager
            .beginTransaction().replace(com.sun.tunnelmonitoring.R.id.fg_tree, TreeFragment.get())
            .commit()
    }

    private fun setupWindowAnimations() {
        val slideTracition = TransitionInflater.from(this).inflateTransition(R.transition.slide_from_right)
        window.enterTransition = slideTracition
        window.exitTransition = slideTracition
    }
}
