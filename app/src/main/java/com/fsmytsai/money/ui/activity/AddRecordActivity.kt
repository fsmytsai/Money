package com.fsmytsai.money.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.fsmytsai.money.R
import kotlinx.android.synthetic.main.activity_add_record.*

class AddRecordActivity : AppCompatActivity() {
    private var mId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        initViews()
    }

    private fun initViews() {
        //設置工具列
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //設置類型改變時顯示隱藏支出類型的下拉式選單
        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_income)
                sp_from.visibility = View.GONE
            else
                sp_from.visibility = View.VISIBLE
        }

        //支出類型的資料陣列
        val fromArray = arrayOf("食", "衣", "住", "行", "育", "樂")
        val arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                fromArray)

        //設置支出類型下拉式選單資料來源
        sp_from.adapter = arrayAdapter

        //如果是修改模式
        if (intent.getBooleanExtra("Edit", false)) {
            //設置工具列文字為修改記錄
            tv_toolBar.text = "修改記錄"
            //取得主頁面傳過來的 Id
            mId = intent.getIntExtra("Id", 0)
            //金額輸入框填入主頁面傳過來的金額
            et_amount.setText(intent.getIntExtra("Amount", 0).toString())

            //簡介輸入框填入主頁面傳過來的簡介
            et_description.setText(intent.getStringExtra("Description"))

            //設置支出類型的下拉式選單預設值
            sp_from.setSelection(fromArray.indexOf(intent.getStringExtra("FromType")))

            //如果主頁面傳過來的類型是 0(收入) 則收入的 radioButton 亮起
            if (intent.getIntExtra("Type", 0) == 0) {
                rb_income.isChecked = true
            } else {
                //否則支出的出入框亮起
                rb_expenses.isChecked = true
            }
        }

        //為 iv_save 設置點擊事件
        iv_save.setOnClickListener { _ ->
            save()
        }
    }

    private fun save() {

        //判斷金額輸入框是否為空，空的話顯示提示並跳出 save function
        if (et_amount.text.toString().isBlank()) {
            Toast.makeText(this, "請輸入金額", Toast.LENGTH_SHORT).show()
            return
        }

        //取得金額輸入框的文字並轉成數字
        val amount = et_amount.text.toString().toInt()

        //判斷當前亮起的是否為收入按鈕，如果是則 type 為 0 ，否則為 1
        val type = if (rg_type.checkedRadioButtonId == R.id.rb_income) 0 else 1

        //取得支出類型
        val fromType = sp_from.selectedItem.toString()

        //取得簡介輸入框的文字
        val description = et_description.text.toString()

        //新建結果意圖的物件
        val resultIntent = Intent()

        //如果 Id 不是 0 則將 Id 放入結果(修改模式 Id 才不為 0)
        if (mId != 0)
            resultIntent.putExtra("Id", mId)

        //將金額放入結果
        resultIntent.putExtra("Amount", amount)
        //將支出類型放入結果
        resultIntent.putExtra("FromType", fromType)
        //將類型放入結果
        resultIntent.putExtra("Type", type)
        //將簡介放入結果
        resultIntent.putExtra("Description", description)
        //設置結果為成功
        setResult(Activity.RESULT_OK, resultIntent)
        //關閉畫面
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        //如果點擊的為返回鈕則返回
        if (id == android.R.id.home) {
            onBackPressed()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
