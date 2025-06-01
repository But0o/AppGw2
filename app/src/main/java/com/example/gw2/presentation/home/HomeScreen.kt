package com.example.gw2.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.utils.ItemViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel,
    onItemClick: (Int) -> Unit,
    onProfileClick: () -> Unit
) {
    // Observamos estados de ViewModels
    val recommendedItems by homeViewModel.recommendedItems.collectAsState()
    val searchQuery by itemViewModel.searchQuery.collectAsState()
    val searchResults by itemViewModel.searchResults.collectAsState()

    // Estado local para controlar la búsqueda
    var localQuery by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ───────────────────────────────────────────────────────────
        // 1) Barra de búsqueda
        OutlinedTextField(
            value = localQuery,
            onValueChange = { newText ->
                localQuery = newText
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar ítem...") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (localQuery.trim().length >= 3) {
                        itemViewModel.updateSearchQuery(localQuery.trim())
                        hasSearched = true
                    }
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (localQuery.trim().length >= 3) {
                        itemViewModel.updateSearchQuery(localQuery.trim())
                        hasSearched = true
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ───────────────────────────────────────────────────────────
        // 2) Si se buscó, muestro los resultados en lista vertical
        if (hasSearched) {
            if (searchResults.isEmpty()) {
                Text(
                    text = "No se encontraron resultados",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            } else {
                LazyColumn {
                    items(searchResults) { item ->
                        ItemRow(item = item, onItemClick = onItemClick)
                    }
                }
            }
        }
        // ───────────────────────────────────────────────────────────
        // 3) Si no se buscó, muestro carruseles de recomendados/favoritos
        else {
            Text(
                text = "🎯 Objetos Recomendados",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recommendedItems) { item ->
                    ItemCard(item = item, onItemClick = onItemClick)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "⭐ Objetos Favoritos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // De momento no hay favoritos, se deja vacío
                val emptyList: List<ItemDetail> = emptyList()
                items(emptyList) { /* placeholder */ }
            }
        }
    }
}

@Composable
fun ItemRow(item: ItemDetail, onItemClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }
            .padding(vertical = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.icon)
                .crossfade(true)
                .build(),
            contentDescription = item.name,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 8.dp)
        )
        Column {
            Text(
                text = item.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (item.type == "Armor" || item.type == "Weapon") {
                    "${item.type} - ${item.details?.type ?: ""}"
                } else {
                    item.type
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ItemCard(item: ItemDetail, onItemClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onItemClick(item.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.icon)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
        }
    }
}
