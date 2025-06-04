// app/src/main/java/com/example/gw2/navigation/Navigation.kt

package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.screens.LoadingScreen
import com.example.gw2.presentation.screens.LoginScreen
import com.example.gw2.presentation.screens.ProfileScreen
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory
import com.example.gw2.utils.SplashViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import com.example.gw2.presentation.utils.HomeViewModelFactory

/**
 * Navigation:
 *  • "splash"  → LoadingScreen con SplashViewModel. Cuando isLoadingComplete==true → "login".
 *  • "login"   → LoginScreen. Al completar login → "home".
 *  • "home"    → HomeScreen(homeVm, itemVm, onItemClick). Con BottomBar.
 *  • "detail/{itemId}" → DetailScreen(itemId).
 *  • "profile" → ProfileScreen(onLogout). Con BottomBar.
 */
@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = androidx.compose.ui.Modifier.padding(padding)
    ) {
        // 1) Splash
        composable("splash") {
            // Creamos SplashViewModel usando un Factory anónimo
            val splashVm: SplashViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SplashViewModel(ItemRepository(RetrofitInstance.api)) as T
                    }
                }
            )

            val currentCount by splashVm.currentCount.collectAsState()
            val totalCount by splashVm.totalCount.collectAsState()
            val isComplete by splashVm.isLoadingComplete.collectAsState()

            LoadingScreen(
                currentCount = currentCount,
                totalCount = totalCount
            )

            LaunchedEffect(isComplete) {
                if (isComplete) {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }

        // 2) Login
        composable("login") {
            LoginScreen(
                onLoggedWithEmail = { _ ->
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

        // 3) Home (aquí inyectamos HomeViewModel + ItemViewModel + onItemClick)
        composable("home") {
            // Creamos el repositorio antes de la fábrica
            val itemRepository = ItemRepository(RetrofitInstance.api)

            // Ahora la fábrica recibe un ItemRepository, tal como definimos en HomeViewModelFactory
            val homeVm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(itemRepository)
            )

            val itemVm: ItemViewModel = viewModel(
                factory = ItemViewModelFactory(itemRepository)
            )

            HomeScreen(
                homeViewModel = homeVm,
                itemViewModel = itemVm,
                onItemClick = { itemId: Int ->
                    navController.navigate("detail/$itemId")
                }
            )
        }

        // 4) DetailScreen (recibe itemId por argumento de ruta)
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            DetailScreen(itemId = itemId.toString())
        }

        // 5) ProfileScreen
        composable("profile") {
            ProfileScreen(onLogout = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}
