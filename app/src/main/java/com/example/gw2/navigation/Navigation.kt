package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gw2.presentation.auth.LoginScreen
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.profile.ProfileScreen
import com.example.gw2.utils.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues,
    itemViewModel: ItemViewModel,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(padding)
    ) {
        composable("home") {
            HomeScreen(
                homeViewModel   = homeViewModel,
                itemViewModel   = itemViewModel,

                // 3) onItemClick: recibe un Int (itemId) y navega a detail/<itemId>
                onItemClick     = { itemId: Int ->
                    navController.navigate("detail/$itemId")
                },

                // 4) onProfileClick: navega a login o a profile según si hay usuario
                onProfileClick  = {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        navController.navigate("login")
                    } else {
                        navController.navigate("profile")
                    }
                }
            )
        }

        // Rutas adicionales… por ejemplo detail, login, profile, etc.
        composable(
            "detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("itemId") ?: 0
            DetailScreen(itemId = id.toString(), itemViewModel = itemViewModel)
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                    navController.navigate("profile")
                },
                onLoginError = { err ->
                    println("❌ Error de login: $err")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}
