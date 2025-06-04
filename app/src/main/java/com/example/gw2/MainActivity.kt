// app/src/main/java/com/example/gw2/MainActivity.kt

package com.example.gw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.gw2.navigation.Navigation
import com.example.gw2.ui.theme.Gw2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Gw2Theme {
                val navController = rememberNavController()
                Navigation(
                    navController = navController,
                    padding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                )
            }
        }
    }
}
