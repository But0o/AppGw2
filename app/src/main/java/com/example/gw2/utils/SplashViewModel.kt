// app/src/main/java/com/example/gw2/utils/SplashViewModel.kt

package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.data.model.ItemDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * SplashViewModel: se encarga de descargar en “chunks” todos los ítems de la API v2,
 * actualizando currentCount/totalCount mientras descarga. Cuando finaliza, isLoadingComplete = true.
 */
class SplashViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount

    private val _isLoadingComplete = MutableStateFlow(false)
    val isLoadingComplete: StateFlow<Boolean> = _isLoadingComplete

    init {
        loadAllItems()
    }

    private fun loadAllItems() {
        viewModelScope.launch {
            try {
                // 1) Traer todos los IDs
                val allIds: List<Int> = repository.api.getAllItemIds()
                _totalCount.value = allIds.size

                // 2) Partir en chunks de 200
                val chunkedIds = allIds.chunked(200).take(8)

                var processed = 0
                // 3) Por cada chunk, pedimos detalles y actualizamos processed
                for (chunk in chunkedIds) {
                    val items: List<ItemDetail> =
                        repository.api.getItemsByIds(chunk.joinToString(","))
                    processed += items.size
                    _currentCount.value = processed
                }

                // 4) Marcamos completado
                _isLoadingComplete.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoadingComplete.value = true
            }
        }
    }
}
