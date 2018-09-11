package com.fsmytsai.money.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.fsmytsai.money.R
import com.fsmytsai.money.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {
    private lateinit var mMyView: View

    private lateinit var mMainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mMyView = inflater.inflate(R.layout.fragment_login, container, false)
        mMainActivity = activity as MainActivity
        initViews()
        return mMyView
    }

    private fun initViews() {
        mMyView.bt_login.setOnClickListener { _ ->
            login()
        }
    }

    private fun login() {
        if (mMyView.et_account.text.toString() == "123" && mMyView.et_password.text.toString() == "456")
            mMainActivity.loginSuccess()
        else
            Toast.makeText(mMainActivity, "帳號或密碼錯誤！", Toast.LENGTH_SHORT).show()
    }
}
