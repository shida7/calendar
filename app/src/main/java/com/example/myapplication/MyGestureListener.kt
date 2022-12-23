package com.example.myapplication

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs


class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffY : Float = e2.y - e1.y
        val diffX : Float = e2.x - e1.x
        val myApp = MyApp.getInstance()

        if(abs(diffX) > 20 && abs(velocityY) > 20 ){
            if(diffY > 0){
                onSwipeBottom()
            }else if(diffY < 0) {
                onSwipeTop()
            }
        } else {
            default()
        }
        Log.d("swipe2", myApp.swipe!!)
        return true
    }
}
fun onSwipeBottom(){
        Log.d("swipe", "down")
        val myApp = MyApp.getInstance()
        myApp.swipe = "down"
    }
fun onSwipeTop(){
        Log.d("swipe", "up")
        val myApp = MyApp.getInstance()
        myApp.swipe = "up"
    }
fun default() {
    Log.d("swipe", "default")
    val myApp = MyApp.getInstance()
    myApp.swipe = "default"
}

