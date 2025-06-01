package com.example.gw2.presentation.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gw2.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onLoginSuccess: (uid: String) -> Unit,
    onLoginError: (message: String) -> Unit
) {
    val context = LocalContext.current
    // Convertir el contexto a Activity para lanzar el Intent
    val activity = context as? Activity

    // 1) Configuramos GoogleSignInOptions
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // 2) Launcher para recibir el resultado de Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            Log.e("LoginError", "Google SignIn cancelado o resultCode != OK (${result.resultCode})")
            onLoginError("Cancelado o falló antes de llegar a Firebase")
            return@rememberLauncherForActivityResult
        }

        try {
            // 3) Extraemos GoogleSignInAccount de la Intent
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(Exception::class.java)

            if (account == null) {
                Log.e("LoginError", "GoogleSignInAccount es null")
                onLoginError("No se pudo obtener la cuenta de Google")
                return@rememberLauncherForActivityResult
            }

            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                Log.e("LoginError", "El idToken recibido de Google es null o vacío")
                onLoginError("No se obtuvo ID Token de Google")
                return@rememberLauncherForActivityResult
            }

            Log.d("LoginDebug", "GoogleSignInAccount obtenido: displayName=${account.displayName}, id=${account.id}")

            // 4) Convertimos ID Token a credencial de Firebase y hacemos sign-in
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            Log.d("LoginDebug", "FirebaseAuth OK – UID=${user.uid}")
                            onLoginSuccess(user.uid)
                        } else {
                            Log.e("LoginError", "FirebaseAuth OK pero currentUser es null")
                            onLoginError("Autenticación OK pero currentUser es null")
                        }
                    } else {
                        val ex = authResult.exception
                        Log.e("LoginError", "Firebase signInWithCredential falló: ${ex?.message}", ex)
                        onLoginError(ex?.localizedMessage ?: "Error desconocido en FirebaseAuth")
                    }
                }
        } catch (e: Exception) {
            Log.e("LoginError", "Excepción en GoogleSignIn: ${e.message}", e)
            onLoginError("Error durante Google Sign-In: ${e.localizedMessage}")
        }
    }

    // 5) UI: un solo botón centrado que lanza el Intent de GoogleSignIn
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                // Lanzamos el Intent de GoogleSignIn
                val signInIntent: Intent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
        ) {
            Text(text = "Iniciar sesión con Google")
        }
    }
}
