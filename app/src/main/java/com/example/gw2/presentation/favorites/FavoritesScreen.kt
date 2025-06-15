// app/src/main/java/com/example/gw2/presentation/favorites/FavoritesScreen.kt
package com.example.gw2.presentation.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.utils.FavoritesViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel,
    onItemClick: (Int) -> Unit
) {
    val favorites by favoritesViewModel.favorites.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Inicia sesión para ver tus favoritos", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes favoritos aún", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item.id) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(item.icon),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
