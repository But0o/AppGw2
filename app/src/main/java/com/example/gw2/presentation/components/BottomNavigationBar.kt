// app/src/main/java/com/example/gw2/presentation/components/BottomNavigationBar.kt

package com.example.gw2.presentation.components

import android.R.attr.icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.gw2.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

/**
 * BottomNavigationBar: muestra tres íconos → Favoritos, Home, Perfil.
 * Para “perfil”, chequeamos si FirebaseAuth.currentUser != null (está logueado con Google/email).
 * Si no, redirigimos a login.
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null && !SessionManager.isGuest

    NavigationBar(
        containerColor = Color(0xFFFFFFFF),    // aquí defines el color de fondo (ejemplo: púrpura oscuro)
        contentColor = Color.White             // color de los íconos/textos dentro
    ) {
        // 1) Favoritos (pendiente de implementar)
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = false,
            onClick = { /* implementación futura */ },
            colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFF000000)
            )
        )

        // 2) Home
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF000000)
            )

        )

        // 3) Perfil
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = {
                if (isLoggedIn) {
                    navController.navigate("profile")
                } else {
                    // Si no hay sesión válida → volvemos a login
                    SessionManager.clear()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF000000)
            )
        )
    }
}
