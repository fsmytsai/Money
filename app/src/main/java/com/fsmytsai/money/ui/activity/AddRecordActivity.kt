package com.fsmytsai.money.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.getBooleanExtra("Edit", false)) {
            tv_toolBar.text = "修改記錄"
            mId = intent.getIntExtra("Id", 0)
            et_amount.setText(intent.getIntExtra("Amount", 0).toString())
            et_description.setText(intent.getStringExtra("Description"))
            if (intent.getIntExtra("Type", 0) == 0)
                rb_income.isChecked = true
            else
                rb_expenses.isChecked = true
        }

        iv_save.setOnClickListener { _ ->
            save()
        }
    }

    private fun save() {
        val type = if (rg_type.checkedRadioButtonId == R.id.rb_income) 0 else 1
        if(et_amount.text.toString().isBlank()){
            Toast.makeText(this, "請輸入金額", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = et_amount.text.toString().toInt()
        val description = et_description.text.toString()
        val resultIntent = Intent()
        if (mId != 0)
            resultIntent.putExtra("Id", mId)
        resultIntent.putExtra("Amount", amount)
        resultIntent.putExtra("Type", type)
        resultIntent.putExtra("Description", description)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == android.R.id.home) {
            onBackPressed()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
