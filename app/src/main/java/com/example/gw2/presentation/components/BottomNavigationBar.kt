// app/src/main/java/com/example/gw2/presentation/components/BottomNavigationBar.kt

package com.example.gw2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gw2.utils.ItemViewModel

/**
 * BottomNavigationBar con tres botones:
 *  - Favoritos  (ruta “favorites” si la tienes implementada; si no, lo dejamos como placeholder)
 *  - Inicio     (ruta “home”; al pulsar “home” limpiamos la búsqueda)
 *  - Perfil     (ruta “profile”)
 *
 * Ahora recibe `itemViewModel` para poder llamar a `updateSearchQuery("")` cuando se presiona “Home”.
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    itemViewModel: ItemViewModel
) {
    // Observamos la ruta actual en el backstack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // — Botón “Favoritos” (placeholder) —
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritos"
                )
            },
            selected = currentRoute == "favorites",
            onClick = {
                // Si tienes una ruta "favorites" definida, aquí navegarías:
                // if (currentRoute != "favorites") {
                //     navController.navigate("favorites")
                // }
            }
        )

        // — Botón “Inicio” —
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            selected = currentRoute == "home",
            onClick = {
                // 1) Limpiamos el texto de búsqueda
                itemViewModel.updateSearchQuery("")
                // 2) Navegamos a "home" (si no estamos ya ahí)
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        // Evitamos apilar varias instancias de "home"
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        )

        // — Botón “Perfil” —
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            selected = currentRoute == "profile",
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate("profile")
                }
            }
        )
    }
}
