// app/src/main/java/com/example/gw2/presentation/components/BottomNavigationBar.kt
package com.example.gw2.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val current = navController.currentBackStackEntry?.destination?.route
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            selected = current == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    if (current == "favorites") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritos"
                )
            },
            selected = current == "favorites",
            onClick = { navController.navigate("favorites") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            selected = current == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}
