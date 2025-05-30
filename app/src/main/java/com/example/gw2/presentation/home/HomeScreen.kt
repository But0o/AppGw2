package com.example.gw2.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gw2.data.RetrofitInstance
import com.example.gw2.presentation.components.ItemCard
import com.example.gw2.data.model.ItemDetail
import com.example.gw2.presentation.components.SearchBar
import com.example.gw2.utils.HomeViewModelFactory


@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(RetrofitInstance.api)
    )
    val items by viewModel.items.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 67.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChanged = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Objetos Recomendados",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        LazyRow {
            items(items) { item ->
                ItemCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Objetos Favoritos", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
    }
}



