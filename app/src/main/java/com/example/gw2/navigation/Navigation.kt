package com.example.gw2.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.home.HomeViewModelFactory
import com.example.gw2.presentation.profile.ProfileScreen
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.screens.LoadingScreen
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    // — Instanciamos los ViewModels aquí —
    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(ItemRepository(RetrofitInstance.api))
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(RetrofitInstance.api)
    )

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.padding(padding)
    ) {
        // --------------------------------------------------
        // 1) Pantalla de carga (“login”)
        composable("login") {
            val isPreloading by itemViewModel.isPreloading.collectAsState()
            val currentCount by itemViewModel.loadedCount.collectAsState()
            val totalCount by itemViewModel.totalCount.collectAsState()

            if (isPreloading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen(
                        currentCount = currentCount,
                        totalCount = totalCount
                    )
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        // --------------------------------------------------
        // 2) Pantalla principal (“home”)
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel,
                onItemClick = { itemId ->
                    navController.navigate("detail/$itemId")
                }
            )
        }

        // --------------------------------------------------
        // 3) Detalle de cada ítem (“detail/{itemId}”)
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailScreen(itemId = itemId)
        }

        // --------------------------------------------------
        // 4) PANTALLA “profile” EXACTAMENTE con ese nombre en la ruta:
        composable("profile") {
            ProfileScreen(
                onLogout = {
                    // Al cerrar sesión, volvemos a “login” y limpiamos “home” de la pila
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
