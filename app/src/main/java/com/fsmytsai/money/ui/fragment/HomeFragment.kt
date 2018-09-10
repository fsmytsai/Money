package com.fsmytsai.money.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
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
        mMyView = inflater.inflate(R.layout.fragment_home, container, false)
        mMainActivity = activity as MainActivity
        mHelper = MyDBHelper(mMainActivity, "money.db", null, 1)
        mCursor = mHelper.readableDatabase.query("record", null,
                null, null, null,
                null, "_id DESC")

        readData()
        initViews()
        return mMyView
    }

    private fun readData() {
        while (mCursor.moveToNext()) {
            mRecordList.add(Record(
                    mCursor.getInt(0),
                    mCursor.getInt(1),
                    mCursor.getString(2),
                    mCursor.getInt(3)
            ))
        }
    }

    private fun initViews() {
        mMyView.tv_add.setOnClickListener { _ ->
            val addIntent = Intent(mMainActivity, AddRecordActivity::class.java)
            startActivityForResult(addIntent, ADD_RECORD)
        }
        mMyView.rv_record.layoutManager = LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false)
        mMyView.rv_record.adapter = RecordAdapter()
    }

    inner class RecordAdapter : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.block_record, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvNo.text = "${mRecordList[position]._id}."
            holder.tvAmount.text = if (mRecordList[position].type == 0)
                "收入：${mRecordList[position].amount}"
            else
                "支出：${mRecordList[position].amount}"

            if (mRecordList[position].description.isBlank())
                holder.tvDescription.visibility = View.GONE
            else {
                holder.tvDescription.visibility = View.VISIBLE
                holder.tvDescription.text = mRecordList[position].description
            }
            holder.llRecord.setOnClickListener { _ ->
                val editIntent = Intent(mMainActivity, AddRecordActivity::class.java)
                editIntent.putExtra("Edit", true)
                editIntent.putExtra("Id", mRecordList[position]._id)
                editIntent.putExtra("Amount", mRecordList[position].amount)
                editIntent.putExtra("Type", mRecordList[position].type)
                editIntent.putExtra("Description", mRecordList[position].description)
                startActivityForResult(editIntent, EDIT_RECORD)
            }

            holder.llRecord.setOnLongClickListener { _ ->
                AlertDialog.Builder(mMainActivity)
                        .setTitle("刪除紀錄")
                        .setMessage("確定刪除紀錄 ${mRecordList[position]._id} 嗎？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("確定") { _, _ ->
                            mHelper.writableDatabase.delete("record", "_id=${mRecordList[position]._id}", null)
                            mRecordList.removeAt(position)
                            mMyView.rv_record.adapter?.notifyItemRemoved(position)
                            if (mRecordList.size - position > 0)
                                mMyView.rv_record.adapter?.notifyItemRangeChanged(position, mRecordList.size - position)
                        }.show()
                true
            }
        }

        override fun getItemCount(): Int {
            return mRecordList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val llRecord = itemView.ll_record!!
            val tvNo = itemView.tv_no!!
            val tvAmount = itemView.tv_amount!!
            val tvDescription = itemView.tv_description!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                ADD_RECORD -> {
                    val type = data.getIntExtra("Type", 0)
                    val amount = data.getIntExtra("Amount", 0)
                    val description = data.getStringExtra("Description")
                    val values = ContentValues()
                    values.put("amount", amount)
                    values.put("description", description)
                    values.put("type", type)
                    val id = mHelper.writableDatabase.insert("record", null, values)
                    mRecordList.add(0, Record(
                            id.toInt(),
                            amount,
                            description,
                            type
                    ))
                    mMyView.rv_record.adapter?.notifyItemInserted(0)
                    mMyView.rv_record.scrollToPosition(0)
                    mMyView.rv_record.adapter?.notifyItemRangeChanged(0, mRecordList.size)
                }
                EDIT_RECORD -> {
                    val id = data.getIntExtra("Id", 0)
                    val type = data.getIntExtra("Type", 0)
                    val amount = data.getIntExtra("Amount", 0)
                    val description = data.getStringExtra("Description")
                    val values = ContentValues()
                    values.put("amount", amount)
                    values.put("description", description)
                    values.put("type", type)
                    mHelper.writableDatabase.update("record", values, "_id=$id", null)
                    val record = mRecordList.find { it._id == id }!!
                    record.amount = amount
                    record.description = description
                    record.type = type
                    mMyView.rv_record.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}
