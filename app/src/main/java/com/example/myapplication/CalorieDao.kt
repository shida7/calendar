package com.example.myapplication

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface CalorieDao {

    @Update
    fun update(calorie: Calorie)

    @Insert
    fun insert(calorie: Calorie)

    @Query("DELETE FROM calorie_table WHERE uid = :key")
    fun delete(key: Int)

    @Query("DELETE FROM calorie_table")
    fun clear()

    @Query("SELECT * FROM calorie_table WHERE date LIKE :key")
    fun getCalorie(key: String): Flowable<List<Calorie>>

    @Query("SELECT * FROM calorie_table ORDER BY date DESC")
    fun getAll(): Flowable<List<Calorie>>

}