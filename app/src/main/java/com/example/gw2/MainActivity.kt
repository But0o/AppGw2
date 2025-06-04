
package com.example.gw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gw2.presentation.MainAppContent
import com.example.gw2.ui.theme.Gw2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Gw2Theme {
                MainAppContent()
            }
        }
    }
}
