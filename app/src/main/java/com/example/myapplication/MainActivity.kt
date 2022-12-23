package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.myapplication.CalorieRoomDatabase.Companion.getDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

//import kotlin.collections.ArrayList


open class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var mCalendarAdapter: CalendarAdapter
    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private lateinit var db: CalorieRoomDatabase
    lateinit var dao : CalorieDao
    private val myApp = MyApp.getInstance()
    private val df: DateFormat = SimpleDateFormat("yyyy/MM/dd")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //DB関連のインスタンス取得
        db = getDatabase(this)
        dao = db.calorieDao()

        //リストの初期表示
        getAllUser(this)

        mCalendarAdapter = CalendarAdapter(this, R.layout.calendar_cell)
        myApp.enterDate = mCalendarAdapter.getCurrentDate()
        Log.d("db2", myApp.dbData.toString())

        val titleText = findViewById<TextView>(R.id.titleText)
        val enterButton = findViewById<Button>(R.id.enterButton)
        //titleText = findViewById(R.id.titleText)
        //calendarGridView = findViewById(R.id.calendarGridView)
        titleText.text = mCalendarAdapter.getTitle()
        gestureDetectorCompat = GestureDetectorCompat(this, MyGestureListener())
        val calendarGridView: GridView = findViewById(R.id.calendarGridView)
        val changeBtn = findViewById<Button>(R.id.changeBtn)
        calendarGridView.setOnTouchListener { _, event ->

            gestureDetectorCompat.onTouchEvent(event)

            if(myApp.swipe == "down"){
                myApp.barCalorie.clear()
                myApp.barDate.clear()
                mCalendarAdapter.prevMonth()
                titleText.text = mCalendarAdapter.getTitle()
            }else if(myApp.swipe == "up"){
                myApp.barCalorie.clear()
                myApp.barDate.clear()
                mCalendarAdapter.nextMonth()
                titleText.text = mCalendarAdapter.getTitle()
            }
            myApp.swipe = "default"
            false
        }
        calendarGridView.adapter = mCalendarAdapter
        calendarGridView.onItemClickListener = this

        enterButton.setOnClickListener {
            startActivity(
                Intent(this, SubActivity::class.java).apply {
                    putExtra("date", myApp.enterDate)
                }
            )
        }
        changeBtn.setOnClickListener {
            val i = Intent(this, GraphActivity::class.java)
            startActivity(i)
        }
    }

    //ListViewにデータを表示する
     @SuppressLint("CheckResult")
     fun getAllUser(context: Context) {
        //ioスレッド：DBからデータ取得
        //mainスレッド：取得結果をUIに表示
        //myApp.dbData = dao.getAll()
        myApp.dbData.clear()
            dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    myApp.dbData.clear()
                    //データ取得完了時の処理
                    it.forEach { user -> myApp.dbData.add(user) }
                    Log.d("date2", myApp.dbData.toString())
                }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetectorCompat.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onStart() {
        mCalendarAdapter.currentMonth()
        val data = mutableListOf(hashMapOf("dish" to "料理名", "calorie" to  "カロリー", "time" to "時間", "id" to "uid", "date" to "data"))
        val indices = myApp.dbData.indices
            .filter { myApp.dbData[it].date == myApp.enterDate }
            .toList()

        indices.forEach { element ->
            data.add(hashMapOf("dish" to myApp.dbData[element].dish, "calorie" to  myApp.dbData[element].dishCalorie.toString(),
                "time" to myApp.dbData[element].time, "id" to myApp.dbData[element].uid.toString(), "date" to myApp.dbData[element].date))
        }
        val list = findViewById<ListView>(R.id.list)
        list.adapter = SimpleAdapter(
            this,
            data,
            R.layout.list_item,
            arrayOf("dish", "calorie", "time"),
            intArrayOf(R.id.dishItem, R.id.calorieItem, R.id.timeItem)
        )
        myApp.barCalorie.clear()
        myApp.barDate.clear()
        super.onStart()
    }
    //タップしたセルと日付を取得する
    @SuppressLint("SimpleDateFormat")
    override fun onItemClick(p0: AdapterView<*>?, parent: View?, position: Int, id: Long) {
        //セルをタップした際のセルのポジション
        Log.d("position", position.toString())
        //セルをタップした際のセルの日付
        Log.d("date", mCalendarAdapter.getItem(position).toString())
        myApp.barCalorie.clear()
        myApp.barDate.clear()
        myApp.touchDate = position
        mCalendarAdapter.currentMonth()
        myApp.enterDate = df.format(mCalendarAdapter.getItem(position))

        val calorieFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        val check: String = calorieFormat.format(mCalendarAdapter.getItem(position))
        val data = mutableListOf(hashMapOf("dish" to "料理名", "calorie" to  "カロリー", "time" to "時間", "id" to "uid", "date" to "data"))
        val indices = myApp.dbData.indices
            .filter { myApp.dbData[it].date == check }
            .toList()

        //var indicesLast = indices.last()

        indices.forEach { element ->
            data.add(hashMapOf("dish" to myApp.dbData[element].dish, "calorie" to  myApp.dbData[element].dishCalorie.toString(),
                "time" to myApp.dbData[element].time, "id" to myApp.dbData[element].uid.toString(), "date" to myApp.dbData[element].date))
        }
        val list = findViewById<ListView>(R.id.list)
        list.adapter = SimpleAdapter(
            this,
            data,
            R.layout.list_item,
            arrayOf("dish", "calorie", "time"),
            intArrayOf(R.id.dishItem, R.id.calorieItem, R.id.timeItem)
        )
        list.setOnItemClickListener { _, _, i, _ ->
            val calorieDate : HashMap<String, String> = data[i]
            if(calorieDate != data[0]){
                startActivity(
                    Intent(this, SubActivity::class.java).apply {
                        putExtra("updateDate", calorieDate)
                    }
                )
            }
        }
    }
}
