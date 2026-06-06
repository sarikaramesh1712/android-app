package com.example.mobileproject.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobileproject.data.SignalIni
import com.example.mobileproject.data.SignalRepository
import kotlinx.coroutines.launch

class SignalViewModel(private val repository: SignalRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _fetchSuccess = MutableLiveData<Boolean>()
    val fetchSuccess: LiveData<Boolean> = _fetchSuccess

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    val allSignals: LiveData<List<SignalIni>> = repository.getAllSignals()

    init {
        // Check if we need to fetch data on initialization
        checkAndFetchData()
    }

    private fun checkAndFetchData() {
        _isLoading.value = true
        _dataState.value = DataState.CHECKING

        viewModelScope.launch {
            try {
                val shouldFetch = repository.shouldFetchData()
                if (shouldFetch) {
                    println("ViewModel: Need to fetch data, starting fetch...")
                    fetchSignals()
                } else {
                    println("ViewModel: Data already available, no fetch needed")
                    _isLoading.value = false
                    _dataState.value = DataState.AVAILABLE
                    _fetchSuccess.value = true
                }
            } catch (e: Exception) {
                println("ViewModel: Error checking data state: ${e.message}")
                _isLoading.value = false
                _errorMessage.value = "Error checking data: ${e.message}"
                _dataState.value = DataState.ERROR
            }
        }
    }

    fun fetchSignals() {
        _isLoading.value = true
        _errorMessage.value = null
        _dataState.value = DataState.FETCHING

        viewModelScope.launch {
            val result = repository.fetchAndStoreAllSignals()
            _isLoading.value = false

            when {
                result.isSuccess -> {
                    _fetchSuccess.value = true
                    _dataState.value = DataState.AVAILABLE
                    println("ViewModel: Signals fetched successfully")
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    _errorMessage.value = error
                    _fetchSuccess.value = false
                    _dataState.value = DataState.ERROR
                    println("ViewModel: Fetch failed - $error")
                }
            }
        }
    }

    fun forceRefresh() {
        _isLoading.value = true
        _errorMessage.value = null
        _dataState.value = DataState.FETCHING

        viewModelScope.launch {
            val result = repository.forceRefreshData()
            _isLoading.value = false

            when {
                result.isSuccess -> {
                    _fetchSuccess.value = true
                    _dataState.value = DataState.AVAILABLE
                    println("ViewModel: Data force refreshed successfully")
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    _errorMessage.value = error
                    _fetchSuccess.value = false
                    _dataState.value = DataState.ERROR
                    println("ViewModel: Force refresh failed - $error")
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _dataState.value = DataState.AVAILABLE
    }

    fun isDataFetched(): Boolean {
        return repository.isDataFetched()
    }

    enum class DataState {
        CHECKING,       // Checking if data needs to be fetched
        FETCHING,       // Currently fetching data
        AVAILABLE,      // Data is available
        ERROR           // Error state
    }
}