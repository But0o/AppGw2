package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.presentation.home.HomeScreen

@Composable
fun Navigation(navController: NavHostController, padding: PaddingValues) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        // Aquí agregarás más pantallas después
    }
}