package com.example.gw2.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.navigation.Navigation
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.home.HomeViewModelFactory
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory

/**
 * Este composable crea el NavController, instancia los ViewModels
 * y muestra el Scaffold con su BottomNavigationBar.
 */
@Composable
fun MainAppContent() {
    // 1) Creamos el NavController
    val navController = rememberNavController()

    // 2) Observamos la ruta actual para decidir si mostramos u ocultamos la BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3) Instanciamos ambos ViewModels
    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(ItemRepository(RetrofitInstance.api))
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(RetrofitInstance.api)
    )

    // 4) Scaffold: aquí pasamos itemViewModel a BottomNavigationBar
    Scaffold(
        bottomBar = {
            // Ocultamos la BottomBar cuando la ruta sea "login"
            if (currentRoute != "login") {
                BottomNavigationBar(
                    navController = navController,
                    itemViewModel = itemViewModel
                )
            }
        }
    ) { paddingValues ->
        // 5) Llamamos a Navigation, que contiene nuestro NavHost
        Navigation(
            navController = navController,
            padding = paddingValues
        )
    }
}
