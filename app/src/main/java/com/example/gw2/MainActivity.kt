package com.example.gw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.MainAppContent
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
                // 1) Instanciamos el ItemViewModel y HomeViewModel
                val itemViewModel: ItemViewModel = viewModel(
                    factory = ItemViewModelFactory(ItemRepository(RetrofitInstance.api))
                )
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(RetrofitInstance.api)
                )

                // 2) Observamos los estados de carga
                val isLoading by itemViewModel.isLoading.collectAsState()
                val progress by itemViewModel.loadingProgress.collectAsState()
                val total by itemViewModel.totalItems.collectAsState()

                // 3) En el primer frame, lanzamos la descarga global
                LaunchedEffect(Unit) {
                    itemViewModel.loadAllItemsAtStartup()
                }

                // 4) Mostramos LoadingScreen mientras isLoading == true
                if (isLoading) {
                    LoadingScreen(
                        currentCount = progress,
                        totalCount = total
                    )
                } else {
                    // 5) Una vez cargado, mostramos la UI normal
                    MainAppContent(
                        itemViewModel = itemViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
