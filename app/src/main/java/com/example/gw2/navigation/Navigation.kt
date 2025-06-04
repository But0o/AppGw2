// app/src/main/java/com/example/gw2/navigation/Navigation.kt

package com.example.gw2.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.auth.LoginScreen
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.screens.LoadingScreen
import com.example.gw2.presentation.screens.ProfileScreen
import com.example.gw2.presentation.utils.HomeViewModelFactory
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory
import com.example.gw2.utils.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.padding(padding)
    ) {
        // ————— 1) Splash —————
        composable("splash") {
            val splashVm: SplashViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SplashViewModel(ItemRepository(RetrofitInstance.api)) as T
                    }
                }
            )

            val currentCount by splashVm.currentCount.collectAsState()
            val totalCount   by splashVm.totalCount.collectAsState()
            val isComplete   by splashVm.isLoadingComplete.collectAsState()

            LoadingScreen(
                currentCount = currentCount,
                totalCount = totalCount
            )

            LaunchedEffect(Unit) {
                // Delay breve para que pinte el primer frame
                delay(200L)
                splashVm.loadAllItems()
            }

            LaunchedEffect(isComplete) {
                if (isComplete) {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }

        // ————— 2) Login —————
        composable("login") {
            LoginScreen(
                onLoggedWithEmail = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoggedWithGoogle = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGuestContinue = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ————— 3) Home con bottomBar —————
        composable("home") {
            // Preparamos repositorio y ViewModels
            val repo = ItemRepository(RetrofitInstance.api)
            val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo))
            val itemVm: ItemViewModel = viewModel(factory = ItemViewModelFactory(repo))

            // Envolvemos HomeScreen en un Scaffold para que aparezca la bottom bar
            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(
                        homeViewModel = homeVm,
                        itemViewModel = itemVm,
                        onItemClick = { itemId ->
                            navController.navigate("detail/$itemId")
                        }
                    )
                }
            }
        }

        // ————— 4) Detail con bottomBar —————
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    DetailScreen(itemId = itemId.toString())
                }
            }
        }

        // ————— 5) Profile con bottomBar —————
        composable("profile") {
            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ProfileScreen(onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}
