package com.example.gw2.data.model

data class RecipeResponse(
    val id: Int,
    val output_item_id: Int,
    val ingredients: List<RecipeIngredient>
)

data class RecipeIngredient(
    val item_id: Int,
    val count: Int
)
