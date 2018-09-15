package com.fsmytsai.money.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fsmytsai.money.R
import com.fsmytsai.money.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {
    private lateinit var mMyView: View

    private lateinit var mMainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //帶入 fragment_login 版面
        mMyView = inflater.inflate(R.layout.fragment_login, container, false)
        mMainActivity = activity as MainActivity
        initViews()
        return mMyView
    }

    private fun initViews() {
        //為 bt_login 設置點擊事件
        mMyView.bt_login.setOnClickListener { _ ->
            login()
        }
    }

    private fun login() {
        //如果帳號是 123 且密碼是 456 則登入成功
        if (mMyView.et_account.text.toString() == "123" && mMyView.et_password.text.toString() == "456")
            mMainActivity.loginSuccess()
        else
            //顯示提示
            Toast.makeText(mMainActivity, "帳號或密碼錯誤！", Toast.LENGTH_SHORT).show()
    }
}
