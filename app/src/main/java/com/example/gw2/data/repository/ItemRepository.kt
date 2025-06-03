package com.example.gw2.data.repository

import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.ItemDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(private val api: IGw2Api) {

    private var allItemsCache: List<ItemDetail>? = null

    /**
     * Devuelve la lista de todos los IDs de ítems.
     */
    suspend fun getAllIds(): List<Int> = api.getAllItemIds()

    /**
     * Precarga TODOS los ítems:
     *   1) obtiene todos los IDs
     *   2) los parte en chunks de 200
     *   3) va acumulando la lista completa en allItemsCache
     *   4) en cada chunk invoca onProgress(loadedSoFar) para reportar cuántos ítems se han cargado hasta el momento.
     */
    suspend fun preloadAllItems(onProgress: (Int) -> Unit): List<ItemDetail> = withContext(Dispatchers.IO) {
        try {
            if (allItemsCache == null) {
                // 1) Pedimos todos los IDs
                val allIds = api.getAllItemIds()
                // 2) Dividimos en trozos de 200 para no explotar la URL
                val chunkedIds = allIds.chunked(200).take(8)
                val allItems = mutableListOf<ItemDetail>()
                var loaded = 0

                chunkedIds.forEach { chunk ->
                    // 3) Por cada chunk pedimos sus detalles
                    val itemsChunk = api.getItemsByIds(chunk.joinToString(","))
                    loaded += itemsChunk.size
                    // 4) Reportamos el progreso (loadedSoFar)
                    onProgress(loaded)
                    allItems.addAll(itemsChunk)
                }

                allItemsCache = allItems
            }
            allItemsCache!!
        } catch (e: Exception) {
            e.printStackTrace()
            allItemsCache = emptyList()
            emptyList()
        }
    }

    /**
     * Busca en la cache de ítems (precargada) todos aquellos cuyo nombre contenga 'query'.
     * Retorna lista vacía si la cache aún no se ha llenado.
     */
    suspend fun searchItemsByName(query: String): List<ItemDetail> = withContext(Dispatchers.Default) {
        try {
            val cache = allItemsCache
            if (cache == null) return@withContext emptyList()
            cache.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
