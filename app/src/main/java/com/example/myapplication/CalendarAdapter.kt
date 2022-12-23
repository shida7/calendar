package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


class CalendarAdapter(private val mContext: Context,
                      private  val resource : Int) : BaseAdapter() {
    private val mDateManager: DateManager = DateManager()
    private var dateArray: List<Date> = mDateManager.getDays()
    private val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val myApp = MyApp.getInstance()



    //カスタムセルを拡張したらここでWidgetを定義
    private class ViewHolder {
        lateinit var dateText: TextView
        lateinit var calorieText : TextView
        lateinit var dateColor: TextView
    }

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getItem(position: Int): Date {
        return dateArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()

    }


    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View{
        val holder: ViewHolder
        val sview = convertView ?: inflater.inflate(resource, null)
        if (convertView == null) {

            holder = ViewHolder()
            holder.dateText = sview.findViewById(R.id.dateText)
            holder.dateColor = sview.findViewById(R.id.dateColor)
            sview.tag = holder
            holder.calorieText = sview.findViewById(R.id.calorieText)

        } else {
            holder = sview.tag as ViewHolder
        }

        holder.dateColor.visibility = View.INVISIBLE

        //セルサイズの指定
        val dp = mContext.resources.displayMetrics.density
        val params : AbsListView.LayoutParams = AbsListView.LayoutParams(
            parent.width / 7 - dp.toInt(),
            (parent.height - dp.toInt() * mDateManager.getWeeks()) / mDateManager.getWeeks()
        )
        sview.layoutParams = params

        //日付のみ表示させる
        val dateFormat = SimpleDateFormat("d", Locale.JAPAN)
        holder.dateText.text = dateFormat.format(dateArray[position])



        //摂取カロリーを記載した日がその日ならカレンダーに記載する
        val calorieFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        val check: String = calorieFormat.format(dateArray[position])
        var calorieTotal = 0
        holder.calorieText.text = ""
        val indices = myApp.dbData.indices
            .filter { myApp.dbData[it].date == check }
            .toList()


        indices.forEach { element ->
            calorieTotal += myApp.dbData[element].dishCalorie
        }

        if(calorieTotal != 0){
            holder.calorieText.text = calorieTotal.toString() + "kcal"
            val format = SimpleDateFormat("yyyy.MM", Locale.JAPAN)
            val checkMonth: String = format.format(dateArray[position])
            if(checkMonth == getTitle()){
                myApp.barDate += dateFormat.format(dateArray[position]) + "日"
                myApp.barCalorie +=calorieTotal
            }
        }

        Log.d("test", indices.toString())
        //当月以外のセルをグレーアウト
        if(mDateManager.isCurrentMonth(dateArray[position])){
            sview.setBackgroundColor(Color.WHITE)
        }else{
            sview.setBackgroundColor(Color.LTGRAY)
        }

        //日曜日を赤、土曜日を青に
        val colorId : Int = when(mDateManager.getDayOfWeek(dateArray[position])){
            1 -> Color.RED
            7 -> Color.BLUE
            else -> Color.BLACK
        }
        holder.dateText.setTextColor(colorId)

        //タップした日付の色を変更する
        if(myApp.touchDate == position){
            holder.dateText.setTextColor(Color.WHITE)
            holder.dateColor.visibility = View.VISIBLE
        }

        return sview
    }
    fun getTitle() :String {
        val format = SimpleDateFormat("yyyy.MM", Locale.JAPAN)
        return format.format(mDateManager.mCalendar.time)
    }
    fun getCurrentDate() :String {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        return format.format(mDateManager.mCalendar.time)
    }

    //翌日を表示
    fun nextMonth() {
        mDateManager.nextMonth()
        dateArray = mDateManager.getDays()
        this.notifyDataSetChanged()
    }

    //前月を表示
    fun prevMonth() {
        mDateManager.prevMonth()
        dateArray = mDateManager.getDays()
        this.notifyDataSetChanged()
    }
    //今月を再ロード
    fun currentMonth() {
        dateArray = mDateManager.getDays()
        this.notifyDataSetChanged()
    }

}