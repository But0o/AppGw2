package com.example.gw2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gw2.data.api.IGw2Api

class HomeViewModelFactory(
    private val api: IGw2Api
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(api) as T
    }
}