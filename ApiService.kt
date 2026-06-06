package com.example.mobileproject.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("size")
    suspend fun getGridSize(): Response<GridSizeResponse>

    // API returns direct array of WiliboxSignal objects
    @GET("wilibox-column")
    suspend fun getColumnSignals(@Query("x") x: Int): Response<List<WiliboxSignal>>
}