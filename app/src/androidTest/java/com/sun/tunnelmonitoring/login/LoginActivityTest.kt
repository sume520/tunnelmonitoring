package com.sun.tunnelmonitoring.login

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.sun.tunnelmonitoring.MainActivity
import com.sun.tunnelmonitoring.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginActivityTest{

    @get:Rule
    var rule= ActivityTestRule(LoginActivity::class.java)


    @Test
    fun LoginTest(){
        //输入账号
        onView(withId(R.id.et_account))
            .perform(clearText(),replaceText("aa"), closeSoftKeyboard())
            .check(matches(withText("aa")))
        //输入密码
        onView(withId(R.id.et_password))
            .perform(clearText(), replaceText("123456"), closeSoftKeyboard())
            .check(matches(withText("123456")))

        onView(withId(R.id.cb_remember_pass))
            .check(matches(isChecked()))

        onView(withId(R.id.cb_autologin))
            .perform(click())
            .check(matches(isChecked()))

        //点击登录按钮
        onView(withId(R.id.bt_login))
            .perform(click())


        Thread.sleep(1000)
    }

}