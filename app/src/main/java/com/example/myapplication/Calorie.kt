package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "calorie_table")
data class Calorie(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val date: String,
    val dish: String,
    val dishCalorie: Int,
    val time: String
)
