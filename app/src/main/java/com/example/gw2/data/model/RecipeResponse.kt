package com.example.gw2.data.model

// Representa la respuesta completa de /v2/recipes?ids=<ids>
data class RecipeResponse(
    val id: Int,
    val type: String,
    val output_item_id: Int,
    val output_item_count: Int,
    val ingredients: List<RecipeIngredient>
)

/** Cada ingrediente en la lista “ingredients” de la receta */
data class RecipeIngredient(
    val item_id: Int,
    val count: Int
)