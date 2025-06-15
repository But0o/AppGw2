// app/src/main/java/com/example/gw2/presentation/home/HomeScreen.kt

package com.example.gw2.presentation.home

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.presentation.components.SearchBar
import com.example.gw2.utils.FavoritesViewModel
import com.example.gw2.utils.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel,
    favoritesViewModel: FavoritesViewModel,
    onItemClick: (Int) -> Unit
) {
    // Observamos el estado de búsqueda y resultados en ItemViewModel
    val searchQuery by itemViewModel.searchQuery.collectAsState()
    val searchResults by itemViewModel.searchResults.collectAsState()

    // Observamos el flujo de ítems recomendados en HomeViewModel
    val recommendedItems by homeViewModel.recommendedItems.collectAsState()
    val favorites  by favoritesViewModel.favorites.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val favoriteItems by favoritesViewModel.favorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1) Barra de búsqueda
        SearchBar(
            query = searchQuery,
            onQueryChange = { itemViewModel.updateSearchQuery(it) },
            onSearch = { /* Podrías forzar la búsqueda manualmente si quisieras */
                // Por ejemplo, aquí podrías llamar a itemViewModel.searchItems(searchQuery)
                // (pero si ya usas updateSearchQuery para cada cambio de texto, probablemente
                //  no necesites hacer nada extra aquí)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotEmpty()) {
            // 2) Si hay texto en búsqueda, mostramos resultados en lista vertical
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron ítems para \"$searchQuery\"",
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn {
                    items(searchResults) { item ->
                        ItemRow(item = item, onClick = onItemClick)
                    }
                }
            }
        } else {
            // 3) Si la búsqueda está vacía, mostramos “Objetos Recomendados”
            Text(
                text = "Objetos Recomendados",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendedItems) { item ->
                    ItemCard(item = item, onClick = onItemClick)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4) Sección de “Objetos Favoritos” (vacía por el momento)
            Text("Objetos Favoritos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (FirebaseAuth.getInstance().currentUser == null) {
                Text("Inicia sesión para guardar favoritos")
            } else if (favoriteItems.isEmpty()) {
                Text("Todavía no tienes favoritos.")
            } else {
                val firstTen = favoriteItems.take(5)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(firstTen) { item ->
                        ItemCard(item = item, onClick = onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: ItemDetail, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .clickable { onClick(item.id) },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.icon),
                contentDescription = item.name,
                modifier = Modifier
                    .size(170.dp)
                    .clip(MaterialTheme.shapes.medium),
                alignment = Alignment.Center

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Left,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ItemRow(item: ItemDetail, onClick: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(item.id) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.icon),
            contentDescription = item.name,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
            )
            val subtypeText = item.details?.type?.let { " - $it" } ?: ""
            Text(
                text = "${item.type}$subtypeText",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
            )
        }
    }
}