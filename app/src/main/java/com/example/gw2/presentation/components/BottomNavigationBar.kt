package com.example.gw2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            selected = false,
            onClick = {
                // Lógica para “Favoritos” (por ahora vacía)
            },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = false,
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            selected = false,
            onClick = {
                // Para “Perfil”, llamamos a un evento en HomeScreen,
                // pero aquí como NavController no tiene referencia de HomeScreen,
                // preferimos usar la ruta directa:
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    navController.navigate("login")
                } else {
                    navController.navigate("profile")
                }
            },
            alwaysShowLabel = false
        )
    }
}
