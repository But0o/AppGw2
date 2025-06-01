package com.example.gw2.data.api

import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IGw2Api {

    @GET("v2/items")
    suspend fun getAllItemIds(): List<Int>

    @GET("v2/items")
    suspend fun getItemsByIds(
        @Query("ids") ids: String
    ): List<ItemDetail>

    @GET("v2/items")
    suspend fun getItemById(
        @Query("id") id: Int
    ): ItemDetail

    @GET("v2/recipes/search")
    suspend fun getRecipeByOutputItemId(
        @Query("output") itemId: Int
    ): List<Int>

    @GET("v2/recipes")
    suspend fun getRecipeById(
        @Query("ids") ids: String
    ): List<RecipeResponse>
}
