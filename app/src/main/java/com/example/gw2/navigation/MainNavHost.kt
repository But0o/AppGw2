package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.data.RetrofitInstance
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.gw2.presentation.home.HomeScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    padding: PaddingValues,
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(padding)
    ) {
        composable("home") {
            // Aquí llamamos a HomeScreen con los tres parámetros exactos:
            HomeScreen(
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel,
                onItemClick = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                favoritesViewModel = TODO()
            )
        }

        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            // Aquí DetailScreen solo recibe un String con el itemId
            DetailScreen(itemId = itemId)
        }
    }
}
