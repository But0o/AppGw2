package com.example.gw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.navigation.MainNavHost
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.home.HomeViewModelFactory
import com.example.gw2.presentation.screens.LoadingScreen
import com.example.gw2.ui.theme.Gw2Theme
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Gw2Theme {
                // Instanciamos los ViewModels
                val itemViewModel: ItemViewModel = viewModel(
                    factory = ItemViewModelFactory(
                        ItemRepository(RetrofitInstance.api)
                    )
                )
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(RetrofitInstance.api)
                )

                // Estados para controlar la carga de todos los ítems al inicio
                val isLoading by itemViewModel.isLoading.collectAsState()
                val progress by itemViewModel.loadingProgress.collectAsState()
                val total by itemViewModel.totalItems.collectAsState()

                // Lanzamos la descarga de ítems sólo una vez cuando la UI se monta
                LaunchedEffect(Unit) {
                    itemViewModel.loadAllItemsAtStartup()
                }

                // Mientras esté cargando, muestro el LoadingScreen
                if (isLoading) {
                    LoadingScreen(
                        currentCount = progress,
                        totalCount = total
                    )
                } else {
                    // Una vez terminada la carga, muestro la app principal con NavHost+BottomBar
                    MainNavHost(
                        itemViewModel = itemViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
