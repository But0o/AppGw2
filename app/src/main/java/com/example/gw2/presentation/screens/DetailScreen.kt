package com.example.gw2.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gw2.data.model.CraftingIngredient
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.utils.ItemViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    itemId: String,
    itemViewModel: ItemViewModel
) {
    // Convertimos itemId a Int para pasarlo al ViewModel
    val itemIdInt = itemId.toIntOrNull()

    // Recibimos los StateFlow y los convertimos a estados Compose
    val itemDetailState by itemViewModel.itemDetail.collectAsState()
    val craftingIngredientsState by itemViewModel.craftingIngredients.collectAsState()

    // Hasta que no tengamos un ID válido, no hacemos nada
    LaunchedEffect(itemIdInt) {
        itemIdInt?.let {
            itemViewModel.loadItemById(it)
        }
    }

    // Si no llegó todavía itemDetail, mostramos un indicador de carga
    if (itemDetailState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Ya tenemos un ItemDetail no nulo
    val item: ItemDetail = itemDetailState!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ** Sección de imagen y datos principales **
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen redondeada
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Tipo: ${item.type}", fontSize = 18.sp)
                item.details?.type?.let { subtype ->
                    Text(text = "Subtipo: $subtype", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rareza: ${item.rarity}", fontSize = 18.sp)
                item.game_types?.let { gameTypes ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Juego: ${gameTypes.joinToString(" - ")}", fontSize = 18.sp)
                }
                item.details?.damage_type?.let { dmgType ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Daño: $dmgType", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Poder: ${item.details?.min_power ?: "-"} - ${item.details?.max_power ?: "-"}",
                    fontSize = 18.sp
                )

                // Atributos (infix_upgrade) si existen
                item.details?.infix_upgrade
                    ?.attributes
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { attrs ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Atributos:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            attrs.forEach { attr ->
                                AssistChip(
                                    onClick = { /* Acciones opcionales */ },
                                    label = { Text("${attr.attribute} +${attr.modifier}") }
                                )
                            }
                        }
                    }
            }
        }

        // ** Sección de ingredientes de crafteo **
        if (craftingIngredientsState.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "🔧 Ingredientes de Crafteo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(craftingIngredientsState) { ingredient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(ingredient.icon)
                                .crossfade(true)
                                .build(),
                            contentDescription = ingredient.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = ingredient.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Cantidad: ${ingredient.count}",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
