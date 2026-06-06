package com.example.mobileproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface LocalSignalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localSignal: LocalSignal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(localSignals: List<LocalSignal>)

    @Update
    suspend fun update(localSignal: LocalSignal)

    @Delete
    suspend fun delete(localSignal: LocalSignal)

    @Query("SELECT * FROM local_signals")
    fun getAllLocalSignals(): LiveData<List<LocalSignal>>

    @Query("SELECT * FROM local_signals WHERE id = :id")
    suspend fun getLocalSignalById(id: Int): LocalSignal?

    @Query("SELECT * FROM local_signals WHERE x = :x AND y = :y")
    fun getLocalSignalsAtCoordinate(x: Int, y: Int): LiveData<List<LocalSignal>>

    @Query("SELECT * FROM local_signals WHERE x BETWEEN :minX AND :maxX AND y BETWEEN :minY AND :maxY")
    fun getLocalSignalsInRange(minX: Int, maxX: Int, minY: Int, maxY: Int): LiveData<List<LocalSignal>>

    @Query("DELETE FROM local_signals WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM local_signals")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM local_signals")
    suspend fun getCount(): Int
}