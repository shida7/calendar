package com.example.myapplication

import android.app.Application
import kotlin.collections.ArrayList

class MyApp : Application() {
    var swipe : String? = null
    //var dbData = ArrayList<Calorie>()
    var dbData = ArrayList<Calorie>()
    var enterDate : String? = null
    var touchDate : Int = 1
    var barCalorie  = ArrayList<Int>()
    var barDate  = ArrayList<String>()
    companion object {
        private var instance : MyApp? = null

        fun getInstance(): MyApp {
            if(instance == null)
                instance = MyApp()

            return instance!!
        }
    }
}