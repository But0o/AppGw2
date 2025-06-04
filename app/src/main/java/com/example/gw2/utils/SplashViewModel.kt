// app/src/main/java/com/example/gw2/utils/SplashViewModel.kt

package com.example.gw2.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.repository.ItemRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount

    private val _isLoadingComplete = MutableStateFlow(false)
    val isLoadingComplete: StateFlow<Boolean> = _isLoadingComplete

    /**
     * Ahora no cargamos en init, sino que lo llamamos manualmente desde el Composable.
     */
    fun loadAllItems() {
        viewModelScope.launch {
            try {
                Log.d("SplashViewModel", "⟳ Iniciando getAllItemIds()…")
                val allIds: List<Int> = repository.getAllItemIds()
                Log.d("SplashViewModel", "✅ getAllItemIds() devolvió ${allIds.size} IDs")
                _totalCount.value = allIds.size

                // Partimos en chunks de 200. Para prueba tomamos solo 10 chunks.
                val chunkedIds = allIds.chunked(200).take(10)
                Log.d("SplashViewModel", "🔹 Total de chunks a procesar: ${chunkedIds.size}")

                var processed = 0
                chunkedIds.forEachIndexed { index, chunk ->
                    Log.d("SplashViewModel", "⟳ Solicitando detalles chunk #${index + 1}…")
                    val items: List<ItemDetail> = repository.getItemsByIds(chunk)
                    processed += items.size
                    _currentCount.value = processed
                    Log.d("SplashViewModel", "✔ Chunk #${index + 1}: procesados ${items.size}, total = $processed")
                }

                Log.d("SplashViewModel", "✔✔ Carga completa. Marcando isLoadingComplete = true")
                _isLoadingComplete.value = true
            }
            catch (ce: CancellationException) {
                // Si cancelan el scope (por ejemplo, navegando fuera de “splash”), relanzamos
                throw ce
            }
            catch (e: Exception) {
                Log.e("SplashViewModel", "❌ Error durante carga de ítems. Marcando isLoadingComplete = true", e)
                _isLoadingComplete.value = true
            }
        }
    }
}
