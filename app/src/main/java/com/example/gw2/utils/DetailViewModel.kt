package com.example.gw2.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.model.RecipeIngredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * DetailViewModel: Carga un ItemDetail y, si el item.type == "Recipe",
 * también carga la lista de ingredientes a través de /v2/recipes/{id}.
 */
class DetailViewModel(private val api: IGw2Api) : ViewModel() {

    /** Representa los estados posibles de la UI de detalle */
    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val item: ItemDetail,
            val recipeIngredients: List<RecipeIngredient>?,
        ) : UiState()
        data class Error(val errorMessage: String) : UiState()
    }

    private val _itemDetailState = MutableStateFlow<UiState>(UiState.Loading)
    val itemDetailState: StateFlow<UiState> = _itemDetailState

    /**
     * Llama primero a getItemById(id). Luego:
     *  • Si item.type == "Recipe", llama a getRecipeById(id) para llenar lista de ingredientes.
     *  • Si no, recipeIngredients será null.
     */
    fun loadItemById(id: Int) {
        viewModelScope.launch {
            _itemDetailState.value = UiState.Loading
            try {
                // 1) Obtenemos el item básico
                val detalle: ItemDetail = api.getItemById(id)

                // 2) Si es un Recipe, pedimos la receta para traer ingredientes
                val listaIngredientes: List<RecipeIngredient>? =
                    try {
                        val receta = api.getRecipeById(id)
                        receta.ingredients
                    } catch (e: Exception) {
                        // Si falla receta, devolvemos lista vacía
                        emptyList()
                    }
                _itemDetailState.value = UiState.Success(
                    item = detalle,
                    recipeIngredients = listaIngredientes
                )
            } catch (e: Exception) {
                val mensaje = e.localizedMessage ?: "Error desconocido"
                _itemDetailState.value = UiState.Error(mensaje)
            }
        }
    }
}
