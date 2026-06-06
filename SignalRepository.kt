package com.example.mobileproject.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignalRepository(
    private val apiService: ApiService,
    private val signalDao: SignalDao,
    private val localSignalDao: LocalSignalDao,
    context: Context
) {

    companion object {
        private const val PREF_DATA_FETCHED = "data_finished"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Existing methods for fetched signals
    fun getAllSignals(): LiveData<List<SignalIni>> {
        return signalDao.getAllSignals()
    }

    // New methods for local signals
    fun getAllLocalSignals(): LiveData<List<LocalSignal>> {
        println("Something is happening!")
        return localSignalDao.getAllLocalSignals()
    }

    suspend fun addLocalSignal(localSignal: LocalSignal): Long {
        return localSignalDao.insert(localSignal)
    }

    suspend fun addLocalSignal(x: Int, y: Int, s1: Int, s2: Int, s3: Int): Long {
        val localSignal = LocalSignal(
            x = x,
            y = y,
            s1 = s1,
            s2 = s2,
            s3 = s3
        )
        return localSignalDao.insert(localSignal)
    }

    suspend fun updateLocalSignal(localSignal: LocalSignal) {
        localSignalDao.update(localSignal)
    }

    suspend fun deleteLocalSignal(localSignal: LocalSignal) {
        localSignalDao.delete(localSignal)
    }

    suspend fun deleteLocalSignalById(id: Int) {
        localSignalDao.deleteById(id)
    }

    suspend fun getLocalSignalById(id: Int): LocalSignal? {
        return localSignalDao.getLocalSignalById(id)
    }

    fun getLocalSignalsAtCoordinate(x: Int, y: Int): LiveData<List<LocalSignal>> {
        return localSignalDao.getLocalSignalsAtCoordinate(x, y)
    }

    suspend fun getLocalSignalsCount(): Int {
        return localSignalDao.getCount()
    }

    suspend fun shouldFetchData(): Boolean {
        return withContext(Dispatchers.IO) {
            // Check if data was already successfully fetched
            val dataFetched = sharedPreferences.getBoolean(PREF_DATA_FETCHED, false)
            if (dataFetched) {
                println("Repository: Data already fetched previously")
                return@withContext false
            }

            // Check if we have data in database
            val signalCount = signalDao.getCount()
            if (signalCount > 0) {
                println("Repository: Found $signalCount signals in database, no need to fetch")
                // Mark as fetched to avoid future checks
                sharedPreferences.edit().putBoolean(PREF_DATA_FETCHED, true).apply()
                return@withContext false
            }

            println("Repository: Need to fetch data - no existing data found")
            true
        }
    }

    suspend fun fetchAndStoreAllSignals(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                println("Repository: Starting data fetch...")

                // Get grid size first
                val sizeResponse: Response<GridSizeResponse> = apiService.getGridSize()
                println("Repository: Grid size response - Success: ${sizeResponse.isSuccessful}")

                if (!sizeResponse.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to get grid size: ${sizeResponse.code()}"))
                }

                val gridSize = sizeResponse.body()!!
                println("Repository: Grid size - X: ${gridSize.minX} to ${gridSize.maxX}, Y: ${gridSize.minY} to ${gridSize.maxY}")

                // Clear existing data
                signalDao.clearAll()

                var totalSignals = 0
                val allSignals = mutableListOf<SignalIni>()

                // Fetch signals for each column
                for (x in gridSize.minX..gridSize.maxX) {
                    try {
                        println("Repository: Fetching column $x...")

                        val response: Response<List<WiliboxSignal>> = apiService.getColumnSignals(x)

                        if (response.isSuccessful) {
                            val signals = response.body() ?: emptyList()
                            val signalIniList = signals.map { wiliboxSignal ->
                                SignalIni(
                                    x = x,
                                    y = wiliboxSignal.y,
                                    s1 = wiliboxSignal.strength1,
                                    s2 = wiliboxSignal.strength2,
                                    s3 = wiliboxSignal.strength3
                                )
                            }

                            allSignals.addAll(signalIniList)
                            totalSignals += signalIniList.size
                            println("Repository: Column $x - ${signalIniList.size} signals")

                        } else {
                            println("Repository: Column $x failed - HTTP ${response.code()}")
                        }

                    } catch (e: Exception) {
                        println("Repository: Error fetching column $x: ${e.message}")
                    }
                }

                // Store all signals at once
                if (allSignals.isNotEmpty()) {
                    signalDao.insertAll(allSignals)

                    // Mark data as successfully fetched
                    sharedPreferences.edit().putBoolean(PREF_DATA_FETCHED, true).apply()

                    println("Repository: Successfully stored $totalSignals signals and marked as fetched")
                    Result.success(true)
                } else {
                    println("Repository: No signals to store!")
                    Result.failure(Exception("No signals found in any column"))
                }

            } catch (e: Exception) {
                println("Repository: Overall error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // Method to manually trigger re-fetch (for pull-to-refresh or settings)
    suspend fun forceRefreshData(): Result<Boolean> {
        // Clear the fetched flag
        sharedPreferences.edit().putBoolean(PREF_DATA_FETCHED, false).apply()
        // Clear existing data
        signalDao.clearAll()
        // Fetch fresh data
        return fetchAndStoreAllSignals()
    }

    suspend fun getStoredSignalsCount(): Int {
        return signalDao.getCount()
    }

    fun isDataFetched(): Boolean {
        return sharedPreferences.getBoolean(PREF_DATA_FETCHED, false)
    }

    fun getAllInitialSignals(): LiveData<List<SignalIni>> {
        return signalDao.getAllSignals()
    }

    // New suspend function for internal use
    suspend fun getAllInitialSignalsOnce(): List<SignalIni> {
        return signalDao.getAllSignalsOnce()
    }
}