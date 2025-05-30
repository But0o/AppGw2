package com.example.gw2.data.api

import com.example.gw2.data.model.ItemDetail
import retrofit2.http.GET
import retrofit2.http.Query

interface IGw2Api {

    // Devuelve la lista de todos los IDs de ítems
    @GET("v2/items")
    suspend fun getAllItemIds(): List<Int>

    // Devuelve los detalles de múltiples ítems por IDs (separados por coma)
    @GET("v2/items")
    suspend fun getItemsByIds(
        @Query("ids") ids: String
    ): List<ItemDetail>

    // También podés tener esta función si buscás uno solo por ID
    @GET("v2/items")
    suspend fun getItemById(
        @Query("id") id: Int
    ): ItemDetail
}

