package com.example.gw2.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.home.HomeViewModel

/**
 * Ahora HomeViewModelFactory recibe un ItemRepository (no un IGw2Api).
 */
class HomeViewModelFactory(
    private val repository: ItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repository) as T
    }
}
