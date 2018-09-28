package com.fsmytsai.money.model

data class Record(
        val _id: Int,//每筆資料皆有 _id，且從 1 開始持續往上編，不重複
        var amount: Int,//金額
        var description: String,//簡介，可為空
        var fromType: String,//支出類型
        var type: Int//0收入 1支出
)