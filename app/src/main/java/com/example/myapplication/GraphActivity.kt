package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


class GraphActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph)
        //表示用サンプルデータの作成//
        val barChart = findViewById<BarChart>(R.id.bar_chat)
        val myApp = MyApp.getInstance()

        myApp.barCalorie.reverse()
        myApp.barDate.reverse()

        //グラフのデータを設定
        val value1: ArrayList<BarEntry> = ArrayList()
        for (i in myApp.barCalorie.indices){
            value1.add(BarEntry(i.toFloat(), myApp.barCalorie[i].toFloat()))
        }

        //chartに設定
        val dataSet1 = BarDataSet(value1, "sample1")
        //値のテキストの大きさ
        dataSet1.valueTextSize = 16f

        dataSet1.color = Color.rgb(255, 212, 0)

        val dataSets: MutableList<IBarDataSet> = ArrayList()
        dataSets.add(dataSet1)

        barChart.data = BarData(dataSets)

        val xAxisFormatter = object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return myApp.barDate[value.toInt()]
            }
        }

        //ラベルを左に表示
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = xAxisFormatter
            labelCount = myApp.barDate.size
            setDrawGridLines(false)
        }

        // Y 軸（左）の設定
        barChart.axisLeft.apply {
            axisMinimum = 0f
        }

        //Y軸（右）の設定
        barChart.axisRight.apply {
            isEnabled = false
        }

        //グラフを表示する
        barChart.invalidate()
    }
}