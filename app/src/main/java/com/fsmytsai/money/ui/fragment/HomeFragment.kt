package com.fsmytsai.money.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fsmytsai.money.R
import com.fsmytsai.money.model.Record
import com.fsmytsai.money.service.app.MyDBHelper
import com.fsmytsai.money.ui.activity.AddRecordActivity
import com.fsmytsai.money.ui.activity.MainActivity
import kotlinx.android.synthetic.main.block_record.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private lateinit var mMyView: View

    private lateinit var mMainActivity: MainActivity

    private lateinit var mHelper: MyDBHelper
    private lateinit var mCursor: Cursor
    private val mRecordList = ArrayList<Record>()

    private val ADD_RECORD = 50
    private val EDIT_RECORD = 60

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //載入 fragment_home 版面
        mMyView = inflater.inflate(R.layout.fragment_home, container, false)
        mMainActivity = activity as MainActivity

        //建立資料庫物件
        mHelper = MyDBHelper(mMainActivity, "money.db", null, 1)

        //建立查詢物件 根據 _id 倒著排序
        mCursor = mHelper.readableDatabase.query("record", null,
                null, null, null,
                null, "_id DESC")

        readData()
        initViews()
        return mMyView
    }

    private fun readData() {
        //用 while 迴圈將資料庫每一筆資料讀出
        while (mCursor.moveToNext()) {
            //將資料塞入 mRecordList，用以顯示
            mRecordList.add(Record(
                    mCursor.getInt(0),
                    mCursor.getInt(1),
                    mCursor.getString(2),
                    mCursor.getInt(3)
            ))
        }
    }

    private fun initViews() {
        //為 tv_add 設置點擊事件
        mMyView.tv_add.setOnClickListener { _ ->
            //建立開啟 AddRecordActivity 頁面的意圖
            val addIntent = Intent(mMainActivity, AddRecordActivity::class.java)
            //開啟並且有返回值，請求值為 ADD_RECORD
            startActivityForResult(addIntent, ADD_RECORD)
        }
        //將 rv_record 設置成直的線性佈局，且不是倒著顯示
        mMyView.rv_record.layoutManager = LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false)
        //將 rv_record 的資料來源設置成 RecordAdapter
        mMyView.rv_record.adapter = RecordAdapter()
    }

    inner class RecordAdapter : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //設置每一列資料的版面皆為 block_record
            val view = LayoutInflater.from(parent.context).inflate(R.layout.block_record, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //設置 tvNo 的文字
            holder.tvNo.text = "${mRecordList[position]._id}."

            //根據 type 設置 tvAmount 的文字及顏色
            if (mRecordList[position].type == 0) {
                holder.tvAmount.text = "收入：${mRecordList[position].amount}"
                holder.tvAmount.setTextColor(Color.GREEN)
                holder.tvNo.setTextColor(Color.GREEN)
            } else {
                holder.tvAmount.text = "支出：${mRecordList[position].amount}"
                holder.tvAmount.setTextColor(Color.RED)
                holder.tvNo.setTextColor(Color.RED)
            }

            //如果簡介為空則隱藏 tvDescription
            if (mRecordList[position].description.isBlank())
                holder.tvDescription.visibility = View.GONE
            else {
                //否則顯示 tvDescription
                holder.tvDescription.visibility = View.VISIBLE
                //設置 tvDescription 的文字
                holder.tvDescription.text = mRecordList[position].description
            }

            //為 btEdit 設置點擊事件
            holder.btEdit.setOnClickListener { _ ->
                //新建開啟 AddRecordActivity 的意圖
                val editIntent = Intent(mMainActivity, AddRecordActivity::class.java)
                //將修改模式設為真
                editIntent.putExtra("Edit", true)
                //放入 Id
                editIntent.putExtra("Id", mRecordList[position]._id)
                //放入金額
                editIntent.putExtra("Amount", mRecordList[position].amount)
                //放入類型
                editIntent.putExtra("Type", mRecordList[position].type)
                //放入簡介
                editIntent.putExtra("Description", mRecordList[position].description)
                //開啟頁面，請求值為 EDIT_RECORD
                startActivityForResult(editIntent, EDIT_RECORD)
            }

            //為 llRecord 設置長按事件
            holder.llRecord.setOnLongClickListener { _ ->

                //顯示視窗
                AlertDialog.Builder(mMainActivity)
                        .setTitle("刪除紀錄")
                        .setMessage("確定刪除紀錄 ${mRecordList[position]._id} 嗎？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("確定") { _, _ ->
                            //根據 _id 去資料庫刪除此筆資料
                            mHelper.writableDatabase.delete("record", "_id=${mRecordList[position]._id}", null)
                            //刪除 mRecordList 中的此筆資料
                            mRecordList.removeAt(position)
                            //告訴 rv_record 此筆資料已被刪除，可以更新頁面了
                            mMyView.rv_record.adapter?.notifyItemRemoved(position)

                            //如果資料列不是最後一筆，則告訴資料源某區段資料已變更
                            if (mRecordList.size - position > 0)
                                mMyView.rv_record.adapter?.notifyItemRangeChanged(position, mRecordList.size - position)
                        }.show()
                true
            }
        }

        override fun getItemCount(): Int {
            //總共要顯示多少列資料
            return mRecordList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val llRecord = itemView.ll_record!!
            val tvNo = itemView.tv_no!!
            val tvAmount = itemView.tv_amount!!
            val btEdit = itemView.bt_edit!!
            val tvDescription = itemView.tv_description!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //如果結果為成功且資料不是空的
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                //新增紀錄
                ADD_RECORD -> {
                    //將從 AddRecordActivity 取得的結果資料帶入變數
                    val type = data.getIntExtra("Type", 0)
                    val amount = data.getIntExtra("Amount", 0)
                    val description = data.getStringExtra("Description")

                    //將資料新增進資料庫
                    val values = ContentValues()
                    values.put("amount", amount)
                    values.put("description", description)
                    values.put("type", type)
                    val id = mHelper.writableDatabase.insert("record", null, values)

                    //將資料新增進 mRecordList
                    mRecordList.add(0, Record(
                            id.toInt(),
                            amount,
                            description,
                            type
                    ))

                    //告訴 rv_record 有資料插入第 0 筆，可以更新頁面了
                    mMyView.rv_record.adapter?.notifyItemInserted(0)
                    //將 rv_record 滾動到 0 的位置
                    mMyView.rv_record.scrollToPosition(0)
                    //告訴 rv_record 的資料源，整個資料列有變動
                    mMyView.rv_record.adapter?.notifyItemRangeChanged(0, mRecordList.size)
                }
                //修改記錄
                EDIT_RECORD -> {
                    //將從 AddRecordActivity 取得的結果資料帶入變數
                    val id = data.getIntExtra("Id", 0)
                    val type = data.getIntExtra("Type", 0)
                    val amount = data.getIntExtra("Amount", 0)
                    val description = data.getStringExtra("Description")

                    //將資料依據 _id 更新進資料庫
                    val values = ContentValues()
                    values.put("amount", amount)
                    values.put("description", description)
                    values.put("type", type)
                    mHelper.writableDatabase.update("record", values, "_id=$id", null)

                    //從 mRecordList 根據 _id 找出要變更的資料
                    val record = mRecordList.find { it._id == id }!!

                    //依序變更
                    record.amount = amount
                    record.description = description
                    record.type = type

                    //告訴 rv_record 的資料源，整個資料列有變動
                    mMyView.rv_record.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}
