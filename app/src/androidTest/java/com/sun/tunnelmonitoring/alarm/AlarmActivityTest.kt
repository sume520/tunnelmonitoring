package com.sun.tunnelmonitoring.alarm

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AlarmActivityTest{
    @get:Rule var rule=ActivityTestRule(AlarmActivity::class.java)

    @Test
    fun AlertTest(){
        onView(withId(R.id.bt_alert))
            .perform(click())
            .check(matches(withText("发出通知")))
    }
}