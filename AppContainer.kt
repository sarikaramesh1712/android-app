package com.example.mobileproject.data

import android.content.Context
import android.content.SharedPreferences

object AppContainer {
    private var database: AppDatabase? = null
    private var repository: SignalRepository? = null
    private var isInitialized = false

    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return

        database = AppDatabase.getInstance(context.applicationContext)
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        repository = SignalRepository(
            RetrofitClient.apiService,
            database!!.signalDao(),
            database!!.localSignalDao(),
            context.applicationContext
        )

        isInitialized = true
        println("AppContainer: Initialization completed")
    }

    fun getRepository(): SignalRepository {
        if (!isInitialized || repository == null) {
            throw IllegalStateException(
                "AppContainer not initialized. Make sure to call AppContainer.initialize(context) in MainActivity.onCreate()"
            )
        }
        return repository!!
    }

    // Helper method to check if initialized
    fun isInitialized(): Boolean = isInitialized
}