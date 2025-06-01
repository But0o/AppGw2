package com.example.gw2.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.gw2.R

@Composable
fun LoadingScreen(
    currentCount: Int,
    totalCount: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Asegúrate de tener ic_gw2_logo.png en res/drawable
            Image(
                painter = painterResource(id = R.drawable.ic_gw2_logo),
                contentDescription = "Logo Guild Wars 2",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Farmeando Objetos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$currentCount / $totalCount",
                fontSize = 18.sp
            )
        }
    }
}
