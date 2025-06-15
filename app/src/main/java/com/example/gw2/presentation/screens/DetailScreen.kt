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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.data.model.RecipeIngredient
import com.example.gw2.utils.DetailViewModel
import com.example.gw2.utils.FavoritesViewModel
import com.example.gw2.utils.FavoritesViewModelFactory
import com.example.gw2.R
import com.example.gw2.data.repository.FavoritesRepository
import com.example.gw2.utils.DetailViewModelFactory

/**
 * DetailScreen: muestra los campos del ítem y, si es Recipe, lista los ingredientes obtenidos
 * desde /v2/recipes/{id}.
 */
@Composable
fun DetailScreen(itemId: String) {
    val detailViewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(RetrofitInstance.api)
    )
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            FavoritesRepository(),
            api = RetrofitInstance.api
        )
    )

    LaunchedEffect(itemId) {
        detailViewModel.loadItemById(itemId.toIntOrNull() ?: 0)
        favoritesViewModel.loadFavorites()
    }

    val uiState by detailViewModel.itemDetailState.collectAsState()

    when (uiState) {
        is DetailViewModel.UiState.Loading -> LoadingPlaceholder()
        is DetailViewModel.UiState.Error -> ErrorPlaceholder((uiState as DetailViewModel.UiState.Error).errorMessage)

        is DetailViewModel.UiState.Success -> {
            val success = uiState as DetailViewModel.UiState.Success

            DetailContent(
                item               = success.item,
                recipeIngredients  = success.recipeIngredients,
                favoritesViewModel = favoritesViewModel
            )
        }
    }
}


@Composable private fun LoadingPlaceholder() {
    Box(Modifier.fillMaxSize().background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
        Text("Cargando detalle…", style = MaterialTheme.typography.bodyLarge)
    }
}
@Composable private fun ErrorPlaceholder(msg:String) {
    Box(Modifier.fillMaxSize().background(Color(0xFFFFEEEE)), contentAlignment = Alignment.Center) {
        Text("Error:\n$msg", color=Color.Red, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun DetailContent(
    item: ItemDetail,
    recipeIngredients: List<RecipeIngredient>?,
    favoritesViewModel: FavoritesViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ————— 1) Tarjeta Superior: Imagen + Nombre + Nivel + Corazón —————
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(item.icon),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = buildString {
                                append(item.type)
                                item.details?.type?.let { append(" – $it") }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Nivel requerido: ${item.level}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Corazón de favorito en la esquina
                FavoriteToggleButton(
                    item = item,
                    favoritesViewModel = favoritesViewModel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ————— 2+3) Wrapper gris para detalles + ingredientes —————
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFCCCCCC))
        ) {
            Column(Modifier.padding(16.dp)) {
                // ——— a) Detalles (rareza, tipos, daño/poder/defensa, atributos) ———
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        // Rareza
                        Text(
                            text = "Rareza: ${item.rarity.replaceFirstChar { it.uppercaseChar() }}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))

                        // Tipo de juego
                        val tipos = item.game_types
                            ?.joinToString(" – ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                            ?: "No disponible"
                        Text("Tipo de juego: $tipos", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))

                        // Daño / Poder / Defensa
                        val details = item.details
                        if (details?.damage_type != null) {
                            Text("Tipo de daño: ${details.damage_type.replaceFirstChar { it.uppercaseChar() }}",
                                style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(6.dp))
                        }
                        if (details?.min_power != null && details.max_power != null) {
                            Text("Poder: ${details.min_power} – ${details.max_power}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                        }
                        if (details?.defense != null) {
                            Text("Defensa: ${details.defense}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                        }

                        // Atributos (solo si hay)
                        details?.infix_upgrade?.attributes?.takeIf { it.isNotEmpty() }?.let { attrs ->
                            Text("Atributos:", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Column {
                                attrs.forEach { attr ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("${attr.attribute.replaceFirstChar { it.uppercaseChar() }} ${attr.modifier}",
                                            style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ——— b) Ingredientes (si receta y tiene ingredientes) ———
                recipeIngredients?.takeIf { it.isNotEmpty() }?.let { ingrList ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 260.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            Text("Ingredientes de crafteo:",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(16.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(ingrList) { ingr ->
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


/** Toggle de favorito en detalle */
@Composable
fun FavoriteToggleButton(
    item: ItemDetail,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    // 1) Observamos la lista de favoritos
    val favList by favoritesViewModel.favorites.collectAsState()

    // 2) Calculamos si este ítem está entre ellos
    val isFav = remember(item.id, favList) { favList.any { it.id == item.id } }

    IconButton(
        onClick = { favoritesViewModel.toggleFavorite(item) },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                id = if (isFav) R.drawable.ic_favorite_24
                else R.drawable.ic_favorite_border_24
            ),
            contentDescription = if (isFav) "Quitar de favoritos" else "Agregar a favoritos"
        )
    }
}
