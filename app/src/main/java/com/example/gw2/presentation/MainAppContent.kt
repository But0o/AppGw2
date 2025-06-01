package com.example.gw2.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gw2.navigation.Navigation
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.utils.ItemViewModel

@Composable
fun MainAppContent(
    itemViewModel: ItemViewModel,
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Navigation(
            navController = navController,
            padding = paddingValues,
            itemViewModel = itemViewModel,
            homeViewModel = homeViewModel
        )
    }
}
