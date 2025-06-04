// app/src/main/java/com/example/gw2/presentation/screens/LoginScreen.kt
package com.example.gw2.presentation.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gw2.R
import com.example.gw2.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoggedWithEmail: (String) -> Unit,
    onLoggedWithGoogle: () -> Unit,
    onGuestContinue: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Configuración de GoogleSignInClient
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleLauncher = rememberLauncherForActivityResult(
        contract = StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    isLoading = true
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(activity) { authTask ->
                            isLoading = false
                            if (authTask.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                firebaseUser?.let { fbUser ->
                                    SessionManager.userId = fbUser.uid
                                    SessionManager.email = fbUser.email
                                    SessionManager.isGuest = false
                                }
                                Toast.makeText(context, "¡Google Sign-In exitoso!", Toast.LENGTH_SHORT).show()
                                onLoggedWithGoogle()
                            } else {
                                Toast.makeText(context, "Error en Google Sign-In", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(context, "Error obteniendo cuenta Google", Toast.LENGTH_SHORT).show()
                Log.e("LoginScreen", "Google sign-in error: $e")
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Crear una cuenta",
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ingresa tu email y contraseña",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de correo
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "email@dominio.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Nuevo: campo de contraseña
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Contraseña (mín. 6 caracteres)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón “Continuar” con email/password
            Button(
                onClick = {
                    if (emailInput.isBlank() || passwordInput.length < 6) {
                        Toast.makeText(
                            context,
                            "Ingresa email válido y contraseña (min 6 caracteres)",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true
                        // Creamos usuario con email/password
                        auth.createUserWithEmailAndPassword(emailInput.trim(), passwordInput)
                            .addOnCompleteListener(activity) { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    firebaseUser?.let { fbUser ->
                                        SessionManager.userId = fbUser.uid
                                        SessionManager.email = fbUser.email
                                        SessionManager.isGuest = false

                                        // Guardar en Firestore: colección "users", doc = uid → {email: ...}
                                        val data = hashMapOf(
                                            "email" to emailInput.trim()
                                        )
                                        firestore.collection("users")
                                            .document(fbUser.uid)
                                            .set(data)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Registrado con correo",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onLoggedWithEmail(emailInput.trim())
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Error guardando en Firestore",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.e("LoginScreen", "Firestore write error: $e")
                                            }
                                    }
                                } else {
                                    // Si ya existe la cuenta, podemos intentar signIn en lugar de create
                                    auth.signInWithEmailAndPassword(emailInput.trim(), passwordInput)
                                        .addOnCompleteListener(activity) { signInTask ->
                                            if (signInTask.isSuccessful) {
                                                val firebaseUser = auth.currentUser
                                                firebaseUser?.let { fbUser ->
                                                    SessionManager.userId = fbUser.uid
                                                    SessionManager.email = fbUser.email
                                                    SessionManager.isGuest = false
                                                }
                                                Toast.makeText(
                                                    context,
                                                    "Sesión iniciada con email",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onLoggedWithEmail(emailInput.trim())
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Error en Auth con email/password",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "Continuar", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Botón “Continuar con Google”
            OutlinedButton(
                onClick = {
                    val signInIntent: Intent = googleClient.signInIntent
                    googleLauncher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Icon(
                    imageVector = Icons.Default.Email, // icono genérico
                    contentDescription = "Ícono Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continuar con Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón “Continuar sin Cuenta”
            OutlinedButton(
                onClick = {
                    SessionManager.clear()
                    SessionManager.isGuest = true
                    onGuestContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Text(text = "Continuar sin Cuenta", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pie de página (opcional)
            Text(
                text = "Al continuar, aceptas nuestros Términos y Política de Privacidad",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp),
                lineHeight = 16.sp
            )
        }
    }
}
