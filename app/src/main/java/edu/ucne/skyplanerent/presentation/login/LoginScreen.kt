package edu.ucne.skyplanerent.presentation.login

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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AdminDTO
import edu.ucne.skyplanerent.data.repository.AdminRepository
import edu.ucne.skyplanerent.presentation.navigation.Screen
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
                                        errorMessage = task.exception?.message ?: "Error de autenticación"
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

