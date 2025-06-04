// app/src/main/java/com/example/gw2/presentation/screens/DetailScreen.kt

package com.example.gw2.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.model.RecipeIngredient
import com.example.gw2.utils.DetailViewModel

/**
 * DetailScreen: muestra los campos del ítem y, si es Recipe, lista los ingredientes obtenidos
 * desde /v2/recipes/{id}.
 */
@Composable
fun DetailScreen(itemId: String) {
    // 1) Creamos el DetailViewModel
    val detailViewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(RetrofitInstance.api)
    )
    // 2) Observamos el estado de UiState
    val uiState by detailViewModel.itemDetailState.collectAsState()

    // 3) Al arrancar, solicitamos la carga del ítem
    LaunchedEffect(itemId) {
        val idInt = itemId.toIntOrNull() ?: 0
        if (idInt > 0) {
            detailViewModel.loadItemById(idInt)
        }
    }

    // 4) Renderizamos Loading / Error / Success
    when (uiState) {
        is DetailViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando detalle…",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is DetailViewModel.UiState.Error -> {
            val message = (uiState as DetailViewModel.UiState.Error).errorMessage
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar detalle:\n$message",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is DetailViewModel.UiState.Success -> {
            // 5) Si es Success, extraemos item y lista de ingredientes (puede ser null)
            val successState = uiState as DetailViewModel.UiState.Success
            DetailContent(
                item = successState.item,
                recipeIngredients = successState.recipeIngredients
            )
        }
    }
}


@Composable
private fun DetailContent(
    item: ItemDetail,
    recipeIngredients: List<RecipeIngredient>? // si no es Recipe o no tiene ingredientes, será null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ————————— 1) Tarjeta Superior: Imagen + Nombre + Tipo/Subtipo + Nivel —————————
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.icon),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append(item.type)
                            item.details?.type?.let { subtype ->
                                append(" – $subtype")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nivel requerido: ${item.level}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ————————— 2) + 3) Card de fondo más oscura que abraza a las dos secciones interiores —————————
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFCCCCCC)) // Gris oscuro de fondo
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // ————— 2) Tarjeta Intermedia: Rareza + Tipo de Juego + (Daño/Poder/Defensa) + Atributos —————
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // a) Rareza
                        Text(
                            text = "Rareza: ${item.rarity.replaceFirstChar { it.uppercaseChar() }}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // b) Tipo de juego
                        val tipos = item.game_types
                            ?.joinToString(separator = " – ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                            ?: "No disponible"
                        Text(
                            text = "Tipo de juego: $tipos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // c) Sección “Daño / Poder / Defensa” (solo si alguno existe)
                        val details = item.details
                        val hasDamage = details?.damage_type != null
                        val hasPower = (details?.min_power != null && details.max_power != null)
                        val hasDefense = details?.defense != null

                        if (hasDamage || hasPower || hasDefense) {
                            if (hasDamage) {
                                val dmg = details!!.damage_type!!
                                Text(
                                    text = "Tipo de daño: ${dmg.replaceFirstChar { it.uppercaseChar() }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (hasPower) {
                                val minPower = details!!.min_power!!
                                val maxPower = details.max_power!!
                                Text(
                                    text = "Poder: $minPower – $maxPower",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (hasDefense) {
                                val def = details!!.defense!!
                                Text(
                                    text = "Defensa: $def",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        // d) Atributos (con encabezado “Atributos” si existen)
                        details?.infix_upgrade?.attributes?.let { attributesList ->
                            if (attributesList.isNotEmpty()) {
                                // → Aquí agregamos el mini título “Atributos”
                                Text(
                                    text = "Atributos:",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Column {
                                    attributesList.forEach { attr ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.15f
                                                    ),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${attr.attribute.replaceFirstChar { it.uppercaseChar() }} ${attr.modifier}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ————— 3) Tarjeta Inferior: Ingredientes de Crafteo (como lista desplazable) —————
                if (!recipeIngredients.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Fijamos una altura máxima para que la lista no ocupe toda la pantalla,
                            // pero sea desplazable si hay muchos ingredientes
                            .heightIn(min = 100.dp, max = 260.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Ingredientes de crafteo:",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            // Usamos LazyColumn para que sea desplazable:
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(recipeIngredients) { ingr ->
                                    // Cada fila de ingrediente:
                                    val ingredienteDetail by produceState<ItemDetail?>(
                                        initialValue = null,
                                        ingr.item_id
                                    ) {
                                        try {
                                            val detalleIngrediente =
                                                RetrofitInstance.api.getItemById(ingr.item_id)
                                            value = detalleIngrediente
                                        } catch (e: Exception) {
                                            value = null
                                        }
                                    }

                                    if (ingredienteDetail == null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Cargando ingrediente ${ingr.item_id}…",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // 1) Imagen del ingrediente
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = ingredienteDetail!!.icon
                                                ),
                                                contentDescription = "Ícono de ${ingredienteDetail!!.name}",
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(6.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))

                                            // 2) Nombre y cantidad
                                            Column {
                                                Text(
                                                    text = ingredienteDetail!!.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Cantidad: ${ingr.count}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
