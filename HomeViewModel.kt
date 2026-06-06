package com.example.mobileproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileproject.data.LocalSignal
import com.example.mobileproject.data.SignalRepository
import kotlinx.coroutines.launch
import kotlin.math.pow

class HomeViewModel (private val repository: SignalRepository) : ViewModel() {
    val allLocalSignals: LiveData<List<LocalSignal>> = repository.getAllLocalSignals()

    private val _localSignalsCount = MutableLiveData<Int>()
    val localSignalsCount: LiveData<Int> = _localSignalsCount

    init {
        // Update count when signals change
        allLocalSignals.observeForever { signals ->
            _localSignalsCount.value = signals.size
        }
    }

    suspend fun addLocalSignal(x: Int, y: Int, s1: Int, s2: Int, s3: Int): Long {
        return repository.addLocalSignal(x, y, s1, s2, s3)
    }

    suspend fun updateLocalSignal(localSignal: LocalSignal) {
        repository.updateLocalSignal(localSignal)
    }

    suspend fun deleteLocalSignal(localSignal: LocalSignal) {
        repository.deleteLocalSignal(localSignal)
    }

    suspend fun deleteLocalSignalById(id: Int) {
        repository.deleteLocalSignalById(id)
    }

    suspend fun getLocalSignalById(id: Int): LocalSignal? {
        return repository.getLocalSignalById(id)
    }

    // Convenience method to add signal using coroutines
    fun addLocalSignalAsync(x: Int, y: Int, s1: Int, s2: Int, s3: Int) {
        viewModelScope.launch {
            try {
                val id = addLocalSignal(x, y, s1, s2, s3)
                println("HomeViewModel: Added local signal with ID: $id")
            } catch (e: Exception) {
                println("HomeViewModel: Error adding local signal: ${e.message}")
            }
        }
    }
    suspend fun computeCoordinatesFromSignals(s1: Int, s2: Int, s3: Int): Pair<Int, Int>? {
        val referenceSignals = repository.getAllInitialSignalsOnce()
        if (referenceSignals.isEmpty()) return null

        var minDistance = Double.MAX_VALUE
        var closestX = 0
        var closestY = 0
        for (ref in referenceSignals) {
            val distance = kotlin.math.sqrt(
                (s1 - ref.s1).toDouble().pow(2) +
                        (s2 - ref.s2).toDouble().pow(2) +
                        (s3 - ref.s3).toDouble().pow(2)
            )
            if (distance < minDistance) {
                minDistance = distance
                closestX = ref.x
                closestY = ref.y
            }
        }

        return closestX to closestY
    }
}

class HomeViewModelFactory(
    private val repository: SignalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}