// app/src/main/java/com/example/gw2/utils/FavoritesViewModel.kt
package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// app/src/main/java/com/example/gw2/utils/FavoritesViewModel.kt

class FavoritesViewModel(
    private val repo: FavoritesRepository,
    private val api: IGw2Api
): ViewModel() {

    private val _favorites = MutableStateFlow<List<ItemDetail>>(emptyList())
    val favorites: StateFlow<List<ItemDetail>> = _favorites

    fun loadFavorites() = viewModelScope.launch {
        try {
            // 1) recupero sólo los IDs
            val ids = repo.getFavoriteIds()
            if (ids.isEmpty()) {
                _favorites.value = emptyList()
            } else {
                // 2) traigo todos sus detalles de golpe
                val items = api.getItemsByIds(ids.joinToString(","))
                _favorites.value = items
            }
        } catch (e: Exception) {
            _favorites.value = emptyList()
        }
    }

    fun toggleFavorite(item: ItemDetail) = viewModelScope.launch {
        val currently = _favorites.value.map { it.id }
        if (currently.contains(item.id)) {
            repo.removeFavorite(item.id)
        } else {
            repo.addFavorite(item.id)
        }
        loadFavorites()  // recargo para que emita el cambio
    }

    class Factory(
        private val repo: FavoritesRepository,
        private val api: IGw2Api
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(repo, api) as T
        }
    }
}

