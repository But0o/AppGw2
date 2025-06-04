// app/src/main/java/com/example/gw2/presentation/home/HomeViewModel.kt

package com.example.gw2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * HomeViewModel: expone un flujo 'recommendedItems' con, por ejemplo,
 * 10 ítems aleatorios cada vez que se crea.
 */
class HomeViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _recommendedItems = MutableStateFlow<List<ItemDetail>>(emptyList())
    val recommendedItems: StateFlow<List<ItemDetail>> = _recommendedItems

    init {
        loadRecommended()
    }

    private fun loadRecommended() {
        viewModelScope.launch {
            try {
                // Ejemplo simple: pedimos todos los IDs, tomamos 10 aleatorios y luego sus detalles
                val allIds = repository.api.getAllItemIds()
                val randomIds = allIds.shuffled().take(10)
                val items = repository.api.getItemsByIds(randomIds.joinToString(","))
                _recommendedItems.value = items
            } catch (e: Exception) {
                e.printStackTrace()
                _recommendedItems.value = emptyList()
            }
        }
    }
}
