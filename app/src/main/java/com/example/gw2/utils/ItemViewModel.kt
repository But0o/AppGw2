package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.model.CraftingIngredient
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    // ─────────────────────────────────────────────────────────────────
    // 1) Estados relacionados con la búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<ItemDetail>>(emptyList())
    val searchResults: StateFlow<List<ItemDetail>> = _searchResults

    // 2) Estado para el ítem actual en DetailScreen
    private val _itemDetail = MutableStateFlow<ItemDetail?>(null)
    val itemDetail: StateFlow<ItemDetail?> = _itemDetail

    // 3) Estado para los ingredientes de crafteo en DetailScreen
    private val _craftingIngredients = MutableStateFlow<List<CraftingIngredient>>(emptyList())
    val craftingIngredients: StateFlow<List<CraftingIngredient>> = _craftingIngredients

    // ─────────────────────────────────────────────────────────────────
    // 4) Estados para la pantalla de carga inicial:
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    // ─────────────────────────────────────────────────────────────────
    /**
     *  Llama al repositorio para obtener TODOS los IDs y luego descaga los ítems en “chunks”.
     *  Durante el bucle, actualiza _loadingProgress con el número total de ítems ya descargados.
     *  Al final, cachea la lista completa en el repositorio y marca isLoading=false.
     */
    fun loadAllItemsAtStartup() {
        viewModelScope.launch {
            try {
                // 1) Obtener TODOS los IDs
                val allIds: List<Int> = repository.getAllItemIds()
                _totalItems.value = allIds.size

                // 2) Recorremos chunks de 200 y vamos acumulando
                val chunkedIds = allIds.chunked(200)
                val fullList = mutableListOf<ItemDetail>()

                for (chunk in chunkedIds) {
                    // 2.1) Descargar este grupo de hasta 200 ítems
                    val items: List<ItemDetail> = repository.getItemsByIds(chunk.joinToString(","))
                    fullList.addAll(items)
                    // 2.2) Actualizar progreso: “X ítems ya descargados”
                    _loadingProgress.value = fullList.size
                }

                // 3) Cachear TODO en memoria (para búsquedas posteriores rápidas)
                repository.cacheAllItems(fullList)
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, podrías decidir cargar parcialmente, o marcar isLoading=false igualmente:
            } finally {
                // 4) Terminó la carga inicial (exit splash)
                _isLoading.value = false
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    /**
     *  Cuando el Home presiona “Buscar”, se invoca este método.
     *  Filtra en la lista cacheada y actualiza _searchResults.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query.trim()

        // Solo filtrar si la consulta NO está vacía
        viewModelScope.launch {
            if (_searchQuery.value.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                val results = repository.searchItemsByName(_searchQuery.value)
                _searchResults.value = results
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    /**
     *  Carga un único ítem por ID y sus ingredientes de crafteo.
     */
    fun loadItemById(itemId: Int) {
        viewModelScope.launch {
            // 1) Detalle del ítem
            val item: ItemDetail = repository.getItemById(itemId)
            _itemDetail.value = item

            // 2) Ingredientes de crafteo
            val ingredients: List<CraftingIngredient> = repository.getCraftingIngredients(itemId)
            _craftingIngredients.value = ingredients
        }
    }
}
