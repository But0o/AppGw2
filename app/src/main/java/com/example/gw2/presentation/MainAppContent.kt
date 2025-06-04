// app/src/main/java/com/example/gw2/presentation/MainAppContent.kt

package com.example.gw2.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gw2.navigation.Navigation
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == "home" || currentRoute == "profile"

    val auth = FirebaseAuth.getInstance()

    // Listener para detectar si hacen signOut “desde afuera” y forzamos volver a login
    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebase ->
            val user = firebase.currentUser
            if (user == null) {
                SessionManager.clear()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                SessionManager.userId = user.uid
                SessionManager.email = user.email
                SessionManager.isGuest = false
            }
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Navigation(
            navController = navController,
            padding = paddingValues
        )
    }

    BackHandler {
        if (currentRoute == "home" || currentRoute == "profile") {
            navController.popBackStack()
        }
    }
}
