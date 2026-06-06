package com.example.mobileproject.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileproject.ui.dashboard.SignalViewModel

class SignalViewModelFactory(
    private val repository: SignalRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalViewModel::class.java)) {
            return SignalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}