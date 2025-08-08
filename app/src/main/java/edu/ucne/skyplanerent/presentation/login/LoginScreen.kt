package edu.ucne.skyplanerent.presentation.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.repository.AdminRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    goToAdminPanel: (Int) -> Unit,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    sessionManager: SessionManager = SessionManager(LocalContext.current),
    adminRepository: AdminRepository
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoskyfleet),
            contentDescription = "Logo de SkyFleet",
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Por favor ingresa un correo electrónico válido"
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }

                if (!isNetworkAvailable(context)) {
                    errorMessage = "Error de conexión. Por favor conéctate a una red"
                    return@Button
                }

                isLoading = true
                errorMessage = null
                adminRepository.getAdminByEmail(email, password).onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> isLoading = true
                        is Resource.Success -> {
                            isLoading = false
                            val admin = resource.data?.firstOrNull()?.adminId
                            if (admin != null) {
                                goToAdminPanel(admin)
                            } else {
                                // No es admin, intentar autenticación con Firebase
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            val user = task.result.user
                                            if (user != null) {
                                                sessionManager.saveAuthState(user)
                                                onLoginSuccess(user.email ?: "")
                                            }
                                        } else {
                                            errorMessage = task.exception?.message ?: "Credenciales inválidas"
                                        }
                                    }
                            }
                        }
                        is Resource.Error -> {
                            isLoading = false
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        val user = task.result.user
                                        if (user != null) {
                                            sessionManager.saveAuthState(user)
                                            onLoginSuccess(user.email ?: "")
                                        }
                                    } else {
                                        errorMessage = getErrorMessage(task.exception)
                                    }
                                }
                        }
                    }
                }.launchIn(CoroutineScope(Dispatchers.Main))
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A80ED),
                contentColor = Color.White
            )
        ) {
            Text(if (isLoading) "Cargando..." else "Iniciar sesión")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "¿No tienes una cuenta? ¡Regístrate!",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToRegister() },
            color = Color.Gray.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


fun getErrorMessage(exception: Exception?): String {
    return when {
        exception == null -> "Error desconocido"
        exception is FirebaseAuthInvalidUserException -> "Cuenta no encontrada. Verifica tu correo."
        exception is FirebaseAuthInvalidCredentialsException -> {
            when {
                exception.message?.contains("email address is badly formatted", ignoreCase = true) == true ->
                    "Formato de correo electrónico inválido"
                exception.message?.contains("password is invalid", ignoreCase = true) == true ->
                    "Contraseña incorrecta"
                else -> "Credenciales inválidas"
            }
        }
        exception is FirebaseNetworkException -> "Sin conexión a internet. Verifica tu red."
        exception.message?.contains("network error", ignoreCase = true) == true ->
            "Problemas de conexión. Verifica tu internet."
        else -> "Error al autenticar: ${exception.localizedMessage ?: "Intenta nuevamente"}"
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}