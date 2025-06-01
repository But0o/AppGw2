package com.example.gw2.data.model

data class ItemDetail(
    val id: Int,
    val name: String,
    val icon: String,
    val type: String,
    val rarity: String,
    val level: Int,
    val details: ItemDetails? = null,
    val game_types: List<String>? = null
)

data class ItemDetails(
    val type: String? = null,
    val damage_type: String? = null,
    val min_power: Int? = null,
    val max_power: Int? = null,
    val infix_upgrade: InfixUpgrade? = null,
    val crafting: Crafting? = null
)

data class InfixUpgrade(
    val attributes: List<ItemAttribute>
)

data class ItemAttribute(
    val attribute: String,
    val modifier: Int
)

data class Crafting(
    val ingredients: List<CraftingIngredient>
)

data class CraftingIngredient(
    val name: String,
    val icon: String,
    val count: Int
)
