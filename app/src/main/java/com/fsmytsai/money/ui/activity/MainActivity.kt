package com.fsmytsai.money.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fsmytsai.money.R
import com.fsmytsai.money.ui.fragment.HomeFragment
import com.fsmytsai.money.ui.fragment.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_main_container, LoginFragment(), "LoginFragment")
                .commit()
    }

    fun loginSuccess(){
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_main_container, HomeFragment(), "HomeFragment")
                .commit()
    }
}
