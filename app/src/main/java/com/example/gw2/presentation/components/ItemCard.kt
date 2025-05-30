package com.example.gw2.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.gw2.data.model.ItemDetail

@Composable
fun ItemCard(item: ItemDetail) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = item.icon,
                contentDescription = item.name,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name)
            Text(text = item.type)
        }
    }
}

