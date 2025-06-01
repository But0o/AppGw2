package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.utils.ItemViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues,
    itemViewModel: ItemViewModel,
    homeViewModel: HomeViewModel
) {
    NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(padding)) {
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel
            ) { itemId ->
                navController.navigate("detail/$itemId")
            }
        }

        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailScreen(
                itemId = itemId,
                itemViewModel = itemViewModel
            )
        }
    }
}
