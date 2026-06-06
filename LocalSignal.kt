package com.example.mobileproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_signals")
data class LocalSignal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val x: Int,
    val y: Int,
    val s1: Int,
    val s2: Int,
    val s3: Int
)