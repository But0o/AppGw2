package com.example.gw2.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.data.repository.ItemRepository
import com.example.gw2.presentation.auth.LoginScreen
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.presentation.favorites.FavoritesScreen
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.home.HomeViewModel
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.screens.LoadingScreen
import com.example.gw2.presentation.screens.ProfileScreen
import com.example.gw2.presentation.utils.HomeViewModelFactory
import com.example.gw2.utils.FavoritesViewModel
import com.example.gw2.utils.FavoritesViewModelFactory
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.utils.ItemViewModelFactory
import com.example.gw2.utils.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gw2.data.repository.FavoritesRepository

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
        // 1) Splash
        composable("splash") {
            // 1) instanciamos el ViewModel con tu Factory
            val splashVm: SplashViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SplashViewModel(ItemRepository(RetrofitInstance.api)) as T
                    }
                }
            )

            // 2) arrancamos la carga UNA sola vez
            LaunchedEffect(Unit) {
                splashVm.loadAllItems()
            }

            // 3) obtenemos los flujos
            val currentCount by splashVm.currentCount.collectAsState()
            val totalCount   by splashVm.totalCount.collectAsState()
            val isComplete   by splashVm.isLoadingComplete.collectAsState()

            // 4) UI
            LoadingScreen(currentCount = currentCount, totalCount = totalCount)

            // 5) cuando termine, navegamos a login
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

        // 3) Home
        composable("home") {
            val repo = ItemRepository(RetrofitInstance.api)
            val homeVm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repo)
            )
            val itemVm: ItemViewModel = viewModel(
                factory = ItemViewModelFactory(repo)
            )
            val favVm: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(
                    repository = FavoritesRepository(),
                    api = RetrofitInstance.api
                )
            )
            LaunchedEffect(FirebaseAuth.getInstance().currentUser) {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    favVm.loadFavorites()
                }
            }

            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { inner ->
                Box(Modifier.padding(inner)) {
                    HomeScreen(
                        homeViewModel      = homeVm,
                        itemViewModel      = itemVm,
                        favoritesViewModel = favVm,
                        onItemClick        = { id -> navController.navigate("detail/$id") }
                    )
                }
            }
        }

        // 4) Favorites
        composable("favorites") {
            val favVm: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(
                    repository = FavoritesRepository(),
                    api = RetrofitInstance.api
                )
            )
            LaunchedEffect(FirebaseAuth.getInstance().currentUser) {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    favVm.loadFavorites()
                }
            }

            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { inner ->
                Box(Modifier.padding(inner)) {
                    FavoritesScreen(
                        favoritesViewModel = favVm,
                        onItemClick        = { id -> navController.navigate("detail/$id") }
                    )
                }
            }
        }

        // 5) Detail
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { inner ->
                Box(Modifier.padding(inner)) {
                    DetailScreen(itemId = itemId.toString())
                }
            }
        }

        // 6) Profile
        composable("profile") {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { inner ->
                Box(Modifier.padding(inner)) {
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
