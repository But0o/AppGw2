package com.example.gw2.presentation.profile

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gw2.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * ProfileScreen:
 * - Si el usuario ya está logueado, muestra nombre, correo y botón “Cerrar sesión”.
 * - Si NO hay usuario, muestra un botón “Iniciar sesión con Google” y gestiona todo el flujo.
 *
 * @param onLogout lambda que se dispara una vez que el usuario se desloguea por completo.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isSigningIn by remember { mutableStateOf(false) }

    // Configuración de GoogleSignIn
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // ActivityResultLauncher para recibir el Intent de GoogleSignIn
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Esto se ejecuta cuando regresa del intent de GoogleSignIn
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Una vez obtenida la cuenta de Google, autenticamos en Firebase
                firebaseAuthWithGoogle(account.idToken!!) {
                    // onSuccess: el usuario ya quedó logueado en Firebase
                    isSigningIn = false
                }
            } catch (e: ApiException) {
                // Falló GoogleSignIn
                e.printStackTrace()
                isSigningIn = false
            }
        } else {
            // El usuario canceló la UI de Google
            isSigningIn = false
        }
    }

    // Obtenemos el usuario actual de Firebase
    val currentUser = auth.currentUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (currentUser != null) {
                // ——————————————————————————————————————————————
                // 1) Usuario ya logueado: mostramos nombre, correo y botón “Cerrar sesión”
                Text(text = "¡Bienvenido, ${currentUser.displayName ?: "Usuario"}!")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Correo: ${currentUser.email ?: "No disponible"}")
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                    // a) Cerrar sesión en FirebaseAuth
                    auth.signOut()

                    // b) Cerrar sesión en GoogleSignInClient (para forzar elección de cuenta la próxima vez)
                    googleSignInClient.signOut().addOnCompleteListener {
                        // c) Invocamos el callback de logout para navegar a “login”
                        onLogout()
                    }
                }) {
                    Text(text = "Cerrar sesión")
                }

            } else {
                // ——————————————————————————————————————————————
                // 2) NO hay usuario: mostramos botón “Iniciar sesión con Google”
                if (isSigningIn) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Iniciando sesión...")
                } else {
                    Button(onClick = {
                        isSigningIn = true
                        // Lanzamos la UI nativa de Google Sign-In
                        val signInIntent: Intent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }) {
                        Text(text = "Iniciar sesión con Google")
                    }
                }
            }
        }
    }
}

/**
 * Autentica en Firebase usando el token de Google.
 *
 * @param idToken token de GoogleSignInAccount.getIdToken()
 * @param onComplete lambda que se llama al terminar la autenticación (exitoso o no).
 */
private fun firebaseAuthWithGoogle(
    idToken: String,
    onComplete: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Si falla el login en Firebase, imprimimos el error
                task.exception?.printStackTrace()
            }
            // Tanto en éxito como en falla cerramos el “isSigningIn”
            onComplete()
        }
}
