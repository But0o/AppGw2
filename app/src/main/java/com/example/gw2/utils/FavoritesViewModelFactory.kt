// app/src/main/java/com/example/gw2/utils/FavoritesViewModelFactory.kt
package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.repository.FavoritesRepository

class FavoritesViewModelFactory(
    private val repository: FavoritesRepository,
    private val api: IGw2Api
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(
            repository,
            api = api
        ) as T
    }

    // ← Esta es la parte que falta: delegar la llamada "extras" a tu otra create(...)
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        return create(modelClass)
    }
}
