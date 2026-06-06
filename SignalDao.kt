package com.example.mobileproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SignalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signals: List<SignalIni>)

    @Query("SELECT * FROM signals")
    fun getAllSignals(): LiveData<List<SignalIni>>

    @Query("SELECT * FROM signals")
    suspend fun getAllSignalsOnce(): List<SignalIni>

    @Query("SELECT * FROM signals WHERE x = :x")
    fun getSignalsByX(x: Int): LiveData<List<SignalIni>>

    @Query("SELECT * FROM signals WHERE x BETWEEN :minX AND :maxX AND y BETWEEN :minY AND :maxY")
    fun getSignalsInRange(minX: Int, maxX: Int, minY: Int, maxY: Int): LiveData<List<SignalIni>>

    @Query("DELETE FROM signals")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM signals")
    suspend fun getCount(): Int
}