package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    // Cuenta de ítems ya cargados
    private val _loadedCount = MutableStateFlow(0)
    val loadedCount: StateFlow<Int> = _loadedCount

    // Total de ítems a cargar
    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    // Flag que indica si la precarga está en curso
    private val _isPreloading = MutableStateFlow(true)
    val isPreloading: StateFlow<Boolean> = _isPreloading

    // Búsqueda por nombre
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Resultados filtrados de búsqueda
    private val _searchResults = MutableStateFlow<List<ItemDetail>>(emptyList())
    val searchResults: StateFlow<List<ItemDetail>> = _searchResults

    init {
        viewModelScope.launch {
            try {
                // 1) Primero obtenemos todos los IDs para saber cuántos hay en total:
                val allIds: List<Int> = repository.getAllIds()
                _totalCount.value = allIds.size

                // 2) Llamamos a preloadAllItems, PASANDO la lambda onProgress:
                repository.preloadAllItems { loadedSoFar ->
                    // Cada vez que se llame onProgress(loadedSoFar), actualizamos _loadedCount
                    _loadedCount.value = loadedSoFar
                }

                // 3) Cuando ha terminado preloadAllItems, marcamos que ya no estamos precargando
                _isPreloading.value = false

                // 4) Si el usuario ya había escrito algo en searchQuery antes de terminar la precarga,
                //    filtramos esos resultados ahora:
                if (_searchQuery.value.isNotEmpty()) {
                    val filtered = repository.searchItemsByName(_searchQuery.value)
                    _searchResults.value = filtered
                }

            } catch (e: Exception) {
                // En caso de fallo, dejamos isPreloading en false para evitar bucles infinitos
                e.printStackTrace()
                _isPreloading.value = false
            }
        }
    }

    /**
     * Actualiza la consulta de búsqueda y, si ya terminó la precarga, filtra los ítems.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            if (_isPreloading.value) {
                // Si todavía estamos precargando, no devolvemos resultados
                _searchResults.value = emptyList()
            } else {
                // Si ya terminó la carga, filtramos localmente en memoria
                val result = repository.searchItemsByName(query)
                _searchResults.value = result
            }
        }
    }
}
