// app/src/main/java/com/example/gw2/data/model/ItemDetail.kt

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
    val type: String? = null,          // subtipo de arma/armadura
    val damage_type: String? = null,   // si es arma
    val min_power: Int? = null,        // si es arma
    val max_power: Int? = null,        // si es arma
    val defense: Int? = null,          // si es armadura

    val infix_upgrade: InfixUpgrade? = null,

    // NOTA: Los “ingredients” de receta ya no irán aquí,
    //       porque la API los provee desde /v2/recipes/{id}.
    // val ingredients: List<CraftingIngredient>? = null // (ya no lo necesitamos)
)

data class InfixUpgrade(
    val attributes: List<ItemAttribute>
)

data class ItemAttribute(
    val attribute: String,
    val modifier: Int
)
