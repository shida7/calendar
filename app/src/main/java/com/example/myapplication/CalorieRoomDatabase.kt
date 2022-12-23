package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Calorie::class], version = 1, exportSchema = false)
abstract class CalorieRoomDatabase : RoomDatabase() {
    abstract fun calorieDao(): CalorieDao

    companion object {
        @Volatile
        private var INSTANCE: CalorieRoomDatabase? = null
        fun getDatabase(context: Context): CalorieRoomDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalorieRoomDatabase::class.java,
                    "calorie_table"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}