package com.example.myapplication

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateManager {
    var mCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

    //当月の要素を取得
    fun getDays(): List<Date>{
        //現在の状態を保持
        val startDate = mCalendar.time

        //GridViewに表示するマスの合計を計算
        val count = getWeeks() * 7

        //当月のカレンダーに表示される前月文の日数を計算
        mCalendar.set(Calendar.DATE, 1)
        val dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1
        mCalendar.add(Calendar.DATE, -dayOfWeek)

        val days: ArrayList<Date> = ArrayList()

        for(i in 1..count ){
            days.add(mCalendar.time)
            mCalendar.add(Calendar.DATE, 1)

        }

        //状態を復元
        mCalendar.time = startDate

        return days
    }

    //当月かどうかを確認
    fun isCurrentMonth(date: Date): Boolean{
        val format = SimpleDateFormat("yyyy.MM", Locale.JAPAN)
        val currentMonth : String = format.format(mCalendar.time)
        return currentMonth == format.format(date)
    }

    //週数を取得
     fun getWeeks(): Int {
        return mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    //曜日の取得
    fun getDayOfWeek(date : Date) : Int{
        val calendar : Calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    //翌月へ
    fun nextMonth(){
        mCalendar.add(Calendar.MONTH, 1)
    }

    //前月へ
    fun prevMonth(){
        mCalendar.add(Calendar.MONTH, -1)
    }
 }

