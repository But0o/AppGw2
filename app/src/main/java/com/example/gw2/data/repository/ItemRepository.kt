package com.example.gw2.data.repository

import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.CraftingIngredient
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.model.RecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(private val api: IGw2Api) {

    // —————————————————————————————————————————————————————————————————————————
    // “Cache” en memoria de todos los ítems: se llena una única vez durante la carga.
    private var allItemsCache: List<ItemDetail>? = null

    /**    Llamada para que el ViewModel guarde la lista completa en el repositorio    */
    fun cacheAllItems(list: List<ItemDetail>) {
        allItemsCache = list
    }

    /**    Si ya hay cache, devuelve; si no, lanza excepción (no se usa para cargar).    */
    fun getCachedItems(): List<ItemDetail> {
        return allItemsCache ?: emptyList()
    }

    /**    Devuelve la lista de todos los IDs (solo sus IDs).    */
    suspend fun getAllItemIds(): List<Int> {
        return api.getAllItemIds()
    }

    /**
     *  Dado un string de IDs separados por coma (“1,2,3,4”), devuelve los ItemDetail
     *  (la API acepta hasta 200 ítems por llamada).
     */
    suspend fun getItemsByIds(ids: String): List<ItemDetail> {
        return api.getItemsByIds(ids)
    }

    /**
     *  Búsqueda local en memoria (cache). Solo filtra en la lista que ya está en cache.
     *  No hace nunca una llamada HTTP. Si no hay cache, devuelve vacío.
     */
    suspend fun searchItemsByName(query: String): List<ItemDetail> = withContext(Dispatchers.Default) {
        val cached = allItemsCache
        if (cached.isNullOrEmpty()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            cached.filter { item ->
                item.name.lowercase().contains(q) ||
                        item.type.lowercase().contains(q) ||
                        (item.details?.type?.lowercase()?.contains(q) == true)
            }
        }
    }

    /**
     *  Obtiene los datos de un ítem por ID (solo un JSON).
     */
    suspend fun getItemById(id: Int): ItemDetail = api.getItemById(id)

    /**
     *  Obtiene los ingredientes de crafteo si tiene receta:
     *  1) Busca recetas con “output=itemId”
     *  2) Obtiene la primera receta (si existe)
     *  3) Descarga los detalles de cada ingrediente
     *  4) Devuelve la lista de CraftingIngredient
     */
    suspend fun getCraftingIngredients(itemId: Int): List<CraftingIngredient> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1) IDs de recetas que dan como output “itemId”
            val recipeIds: List<Int> = api.getRecipeByOutputItemId(itemId)
            if (recipeIds.isEmpty()) {
                emptyList()
            } else {
                // 2) Tomamos la primera receta
                val recipeResponse: RecipeResponse = api.getRecipeById(recipeIds.first().toString()).first()
                // 3) Descargamos los detalles de cada ingrediente por su item_id
                val ingredientIds: List<Int> = recipeResponse.ingredients.map { it.item_id }
                val allIngredientDetails: List<ItemDetail> = api.getItemsByIds(ingredientIds.joinToString(","))

                // 4) Armamos lista de CraftingIngredient
                recipeResponse.ingredients.mapNotNull { ing ->
                    val detail = allIngredientDetails.find { it.id == ing.item_id }
                    detail?.let {
                        CraftingIngredient(
                            name = it.name,
                            icon = it.icon,
                            count = ing.count
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
