package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat


class SubActivity : AppCompatActivity(){
    private lateinit var db: CalorieRoomDatabase
    lateinit var dao : CalorieDao
    private val myApp = MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_enter)

        //DB関連のインスタンス取得
        db = CalorieRoomDatabase.getDatabase(this)
        dao = db.calorieDao()

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val morningBtn = findViewById<RadioButton>(R.id.morningBtn)
        val noonBtn = findViewById<RadioButton>(R.id.noonBtn)
        val nightBtn = findViewById<RadioButton>(R.id.nightBtn)
        val saveBtn = findViewById<Button>(R.id.saveButton)
        val date = findViewById<Button>(R.id.dateTxt)
        val dish = findViewById<EditText>(R.id.dishEdit)
        val calorie = findViewById<EditText>(R.id.calorieEdit)
        var uid : String = "1"
        var deletebtn = findViewById<Button>(R.id.deleteBtn)


        if(intent.getStringExtra("date") != null){
            date.text = intent.getStringExtra("date")
            radioGroup.check(morningBtn.id)
        } else{
            val intent = intent
            val hashMap = intent.getSerializableExtra("updateDate") as HashMap<*, *>?
            dish.setText(hashMap?.get("dish").toString())
            calorie.setText(hashMap?.get("calorie").toString())
            date.text = hashMap?.get("date").toString()
            uid = hashMap?.get("id").toString()
            if(hashMap?.get("time").toString() == "朝"){
                radioGroup.check(morningBtn.id)
            } else if(hashMap?.get("time").toString() == "昼") {
                radioGroup.check(noonBtn.id)
            } else {
                radioGroup.check(nightBtn.id)
            }

        }
        date.setOnClickListener {

            DatePickerFragment().apply {
                //フラグメントに値を渡す
                arguments = Bundle().apply {
                    putString("defaultDate", date.text as String?)
                }
                show(supportFragmentManager, "dialog_basic")
            }
        }
        saveBtn.setOnClickListener {
            val calorieStr : String = calorie.text.toString()
            val id = radioGroup.checkedRadioButtonId
            val checkedRadioButton = findViewById<RadioButton>(id)
            if(intent.getStringExtra("date") != null){
                inCalorie(date.text.toString(), dish.text.toString(), calorieStr.toInt(), checkedRadioButton.text.toString())
            } else {
                updateCalorie(uid.toInt(), date.text.toString(), dish.text.toString(), calorieStr.toInt(), checkedRadioButton.text.toString())
            }

            getAllUser(this)
            finish()
        }
        if(intent.getStringExtra("date") == null){
            deletebtn.visibility = View.VISIBLE
            deletebtn.setOnClickListener {
                Log.d("ok", "ok")
                AlertDialog.Builder(this)
                    .setTitle("このカロリーを削除しますか？")
                    .setPositiveButton("はい") { _, _ ->
                        deleteCalorie(uid.toInt())
                        getAllUser(this)
                        finish()
                    }
                    .setNegativeButton("いいえ") { _, _ ->
                    }
                    .show()

            }
        }else{
            deletebtn.visibility = View.INVISIBLE
        }
    }
    //入力内容をDBに登録する
    private fun inCalorie(data : String, dish: String, calorie: Int, time: String){
        Completable.fromAction{dao.insert(Calorie(0, data, dish, calorie, time))}
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
    //カロリーを編集する
    private fun updateCalorie(id : Int, data : String, dish: String, calorie: Int, time: String){
        Completable.fromAction{dao.update(Calorie(id, data, dish, calorie, time))}
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    //カロリーを削除する
    private fun deleteCalorie(id : Int){
        Completable.fromAction{dao.delete(id)}
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

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
}

