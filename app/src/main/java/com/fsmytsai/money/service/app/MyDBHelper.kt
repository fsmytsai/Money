package com.fsmytsai.money.service.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE record " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "amount INTEGER NOT NULL , " +
                "description TEXT NOT NULL , " +
                "type INTEGER NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
