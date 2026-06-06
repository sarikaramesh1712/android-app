package com.example.mobileproject.data
data class GridSizeResponse(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int
)
data class WiliboxSignal(
    val x: Int,
    val y: Int,
    val strength1: Int,
    val strength2: Int,
    val strength3: Int
)

