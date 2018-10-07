# 新增支出類型功能

## 於資料庫及紀錄的資料類別中加入支出類型欄位

```kotlin
//MyDBHelper.kt
db.execSQL("CREATE TABLE record " +
        "(_id INTEGER PRIMARY KEY  NOT NULL , " +
        "amount INTEGER NOT NULL , " +
        "description TEXT NOT NULL , " +
        "from_type TEXT NOT NULL , " +
        "type INTEGER NOT NULL)")

//Record.kt
data class Record(
        val _id: Int,//每筆資料皆有 _id，且從 1 開始持續往上編，不重複
        var amount: Int,//金額
        var description: String,//簡介，可為空
        var fromType: String,//支出類型
        var type: Int//0收入 1支出
)

//HomeFragment.kt
mRecordList.add(Record(
        mCursor.getInt(0),
        mCursor.getInt(1),
        mCursor.getString(2),
        mCursor.getString(3),
        mCursor.getInt(4)
))

mRecordList.add(0, Record(
        id.toInt(),
        amount,
        description,
        "",
        type
))
```

## 在新增紀錄的頁面加入下拉式選單(activity_add_record.xml)

可放置在自己覺得適合的地方，屬性也可自由調整。
```xml
<Spinner
    android:id="@+id/sp_from"
    android:layout_width="120dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:layout_marginTop="10dp" />
```

## 在程式碼中填入下拉式選單該有的資料(AddRecordActivity.kt)

```kotlin
val fromArray = arrayOf("食", "衣", "住", "行", "育", "樂")
var arrayAdapter = ArrayAdapter(this,
        android.R.layout.simple_spinner_dropdown_item,
        fromArray)
sp_from.adapter = arrayAdapter
```

## 設置當選擇支出單選按鈕時才顯示下拉式選單(AddRecordActivity.kt)

```kotlin
rg_type.setOnCheckedChangeListener { _, checkedId ->
    if (checkedId == R.id.rb_income)
        sp_from.visibility = View.GONE
    else
        sp_from.visibility = View.VISIBLE
}
```

## 將用戶選擇的結果回傳給主頁面(AddRecordActivity.kt)

```kotlin
val fromType = sp_from.selectedItem.toString()
resultIntent.putExtra("FromType", fromType)
```

## 取得新增紀錄頁面回傳的資料(HomeFragment.kt)

```kotlin
//ADD_RECORD
val fromType = data.getStringExtra("FromType")
values.put("from_type", fromType)
mRecordList.add(0, Record(
        id.toInt(),
        amount,
        description,
        fromType,
        type
))

//EDIT_RECORD
val fromType = data.getStringExtra("FromType")
values.put("from_type", fromType)
record.fromType = fromType
```

## 顯示支出類型(HomeFragment.kt)

```kotlin
holder.tvAmount.text = "支出(${mRecordList[position].fromType})：${mRecordList[position].amount}"
```

## 將支出類型放入修改資料(HomeFragment.kt)

```kotlin
editIntent.putExtra("FromType", mRecordList[position].fromType)
```

## 修改時預設支出類型(AddRecordActivity.kt)

```kotlin
sp_from.setSelection(fromArray.indexOf(intent.getStringExtra("FromType")))
```

# 新增收入類型功能

## 新增收入類型陣列(AddRecordActivity.kt)

```kotlin
private lateinit var fromArray: Array<String>
fromArray = if (intent.getIntExtra("Type", 0) == 0)
    arrayOf("薪水", "投資", "零用錢", "其他")
else
    arrayOf("食", "衣", "住", "行", "育", "樂")
```

## 修改收入、支出單選按鈕的切換監聽事件(AddRecordActivity.kt)

```kotlin
rg_type.setOnCheckedChangeListener { _, checkedId ->
    fromArray = if (checkedId == R.id.rb_income)
        arrayOf("薪水", "投資", "零用錢", "其他")
    else
        arrayOf("食", "衣", "住", "行", "育", "樂")

    arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            fromArray)

    //設置下拉式選單資料來源
    sp_from.adapter = arrayAdapter
}
```

顯示收入類型(HomeFragment.kt)

```kotlin
holder.tvAmount.text = "收入(${mRecordList[position].fromType})：${mRecordList[position].amount}"
```
