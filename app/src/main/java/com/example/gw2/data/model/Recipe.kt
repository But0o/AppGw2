package com.example.gw2.data.model

data class Recipe(
    val id: Int,
    val output_item_id: Int,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val item_id: Int,
    val count: Int
)
