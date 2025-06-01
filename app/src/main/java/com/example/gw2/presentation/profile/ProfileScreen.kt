package com.example.gw2.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.gw2.R

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Text(text = "¡Bienvenido, ${currentUser.displayName ?: "Usuario"}!")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Correo: ${currentUser.email ?: "No disponible"}")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                // 1) Cerrar sesión en FirebaseAuth
                FirebaseAuth.getInstance().signOut()

                // 2) Cerrar sesión en GoogleSignInClient para forzar selección de cuenta
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    // 3) Luego de cerrar sesión en Google, invocamos el callback para navegar
                    onLogout()
                }
            }) {
                Text(text = "Cerrar sesión")
            }
        } else {
            Text(text = "No hay usuario logueado")
        }
    }
}
