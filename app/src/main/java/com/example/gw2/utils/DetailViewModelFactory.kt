package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gw2.data.api.IGw2Api

class DetailViewModelFactory(
    private val api: IGw2Api
) : Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(api) as T
    }

    // Necesario para Navigation+Compose en versiones recientes
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T = create(modelClass)
}