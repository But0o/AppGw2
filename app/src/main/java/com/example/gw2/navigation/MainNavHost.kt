package com.example.gw2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.gw2.R
import com.example.gw2.presentation.auth.LoginScreen
import com.example.gw2.presentation.home.HomeScreen
import com.example.gw2.presentation.profile.ProfileScreen
import com.example.gw2.presentation.screens.DetailScreen
import com.example.gw2.presentation.components.BottomNavigationBar
import com.example.gw2.utils.ItemViewModel
import com.example.gw2.presentation.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun MainNavHost(
    itemViewModel: ItemViewModel,
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    // Capturamos el context ANTES de la lambda para poder usarlo al hacer signOut
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomAppBar {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ───────────────────────────────────────────────────────────
            composable("home") {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    itemViewModel = itemViewModel,
                    onItemClick = { itemId ->
                        navController.navigate("detail/$itemId")
                    },
                    onProfileClick = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser == null) {
                            navController.navigate("login")
                        } else {
                            navController.navigate("profile")
                        }
                    }
                )
            }

            // ───────────────────────────────────────────────────────────
            composable(
                "detail/{itemId}",
                arguments = listOf(navArgument("itemId") {
                    type = NavType.IntType
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("itemId") ?: 0
                DetailScreen(
                    itemId = id.toString(),
                    itemViewModel = itemViewModel
                )
            }

            // ───────────────────────────────────────────────────────────
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.popBackStack()
                        navController.navigate("profile")
                    },
                    onLoginError = { errorMsg ->
                        println("❌ Error de login: $errorMsg")
                    }
                )
            }

            // ───────────────────────────────────────────────────────────
            composable("profile") {
                ProfileScreen(onLogout = {
                    // 1) Cerramos sesión en FirebaseAuth
                    FirebaseAuth.getInstance().signOut()

                    // 2) Cerramos sesión en GoogleSignInClient para forzar selector de cuenta
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        // Usamos R.string.default_web_client_id (no "string.default_web_client_id")
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut().addOnCompleteListener {
                        // 3) Una vez cerrada la sesión en Google, navegamos de vuelta a "home"
                        navController.popBackStack("home", inclusive = false)
                    }
                })
            }
        }
    }
}
