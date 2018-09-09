package com.fsmytsai.money.model

data class Record(
        val _id: Int,
        var amount: Int,
        var description: String,
        var type: Int//0收入 1支出
)