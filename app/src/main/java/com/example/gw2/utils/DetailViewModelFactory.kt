package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gw2.data.api.IGw2Api

class DetailViewModelFactory(
    private val api: IGw2Api
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(api) as T
    }
}

